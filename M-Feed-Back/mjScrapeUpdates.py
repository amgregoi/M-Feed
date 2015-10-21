#! /usr/bin/env python
import csv,time,re,json,httplib,urllib,requests
from BeautifulSoup import BeautifulSoup
			
	
#setup
url = 'http://www.manga-joy.com'
response = requests.get(url)
html = response.content
soup = BeautifulSoup(html)

#List of latest chapters
table = soup.find('div', attrs={'class' : 'wpm_pag mng_lts_chp grp'})
rows = table.findAll('div', attrs={'class' : 'row'})

#Get picture url
def getPic(index):
	mPic = rows[index].find('img')["src"]
	mPic = mPic.replace("_30x0.jpg", "_198x0.jpg")
	return mPic


#Get title
def getTitle(index):
	mTitle = rows[index].find('a', attrs={'class' : 'ttl mng_det_pop'}).get("title")
	mTitle = mTitle.strip()
	return mTitle	


#Get chapter number
def getLatestChapter(index):
	mChaps = rows[index].findAll('a', attrs={'class' : 'lst'})
	#0 latest chapter for multiple chapter updates
	mLatestChap = mChaps[0].find('b', attrs={'class' : 'val lng_'}).text
	mSplit = mLatestChap.split(' ') #ex: Chapter 17 - chapter title
	return mSplit[1]

#Get update date (1/1/2015)
def getDate():
	date = time.strftime("%m/%d/%Y")
	return date	

#Get url
def getUrl(index):
	mUrl = rows[index].find('a', attrs={'class' : 'ttl mng_det_pop'})['href']
	return mUrl

def parseUpdateManga(nChapter, nDate, objId, mTitle):
	connection = httplib.HTTPSConnection('api.parse.com', 443)
	connection.connect()
	connection.request('PUT', '/1/classes/Manga/'+objId, json.dumps({
	       "LatestChapter":nChapter,
               "DateUpdated":nDate
	     }), {
	       "X-Parse-Application-Id": "gr2JB7mjsp4PhtSP0fiiS02XMhQBzO6g3siQJ5nS",
	       "X-Parse-REST-API-Key": "f4GVD94FFVQ6lRdJeenZ3RWbBBMQMcyXUFDyRlZ2",
	       "Content-Type": "application/json"
	     })
	parsePush(objId, mTitle)
	result = json.loads(connection.getresponse().read())
	#print result


def parsePush(objId, title):
	connection = httplib.HTTPSConnection('api.parse.com', 443)
	connection.connect()
	connection.request('POST', '/1/push', json.dumps({
	       "channels": [
		 "m_"+objId
	       ],
	       "data": {
		 "alert": title+" has just been updated, come check it out!"
	       }
		     }), {
	       "X-Parse-Application-Id": "gr2JB7mjsp4PhtSP0fiiS02XMhQBzO6g3siQJ5nS",
	       "X-Parse-REST-API-Key": "f4GVD94FFVQ6lRdJeenZ3RWbBBMQMcyXUFDyRlZ2",
	       "Content-Type": "application/json"
	     })
	result = json.loads(connection.getresponse().read())
	#print result


#if not already in library, but in updates, adds to database
def parseAddManga(i):
	connection = httplib.HTTPSConnection('api.parse.com', 443)
	connection.connect()
	connection.request('POST', '/1/classes/Manga', json.dumps({
	       "PictureUrl": getPic(i),
	       "MangaTitle": getTitle(i),
	       "LatestChapter": getLatestChapter(i),
               "DateUpdated" : getDate(),
               "MangaUrl" : getUrl(i),
               "Section": getTitle(i)[0]
	     }), {
	       "X-Parse-Application-Id": "gr2JB7mjsp4PhtSP0fiiS02XMhQBzO6g3siQJ5nS",
	       "X-Parse-REST-API-Key": "f4GVD94FFVQ6lRdJeenZ3RWbBBMQMcyXUFDyRlZ2",
	       "Content-Type": "application/json"
	     })
	results = json.loads(connection.getresponse().read())
	#print results

#check if title already exists in parse db
def parseGetManga(title):
	connection = httplib.HTTPSConnection('api.parse.com', 443)
	params = urllib.urlencode({"where":json.dumps({
	       'MangaTitle':title
	     })})
	connection.connect()
	connection.request('GET', '/1/classes/Manga?%s' % params, '', {
	       "X-Parse-Application-Id": "gr2JB7mjsp4PhtSP0fiiS02XMhQBzO6g3siQJ5nS",
	       "X-Parse-REST-API-Key": "f4GVD94FFVQ6lRdJeenZ3RWbBBMQMcyXUFDyRlZ2"
	     })
	result = json.loads(connection.getresponse().read())
	connection.close()
	return result
	


def parseGetLatestChapter(mTitle):
	connection = httplib.HTTPSConnection('api.parse.com', 443)
	params = urllib.urlencode({"where":json.dumps({
	       'MangaTitle':mTitle
	     }),'limit':limit,'skip':skip, 'order':'createdAt'})
	connection.connect()
	connection.request('GET', '/1/classes/Manga?%s' % params, '', {
	       "X-Parse-Application-Id": "gr2JB7mjsp4PhtSP0fiiS02XMhQBzO6g3siQJ5nS",
	       "X-Parse-REST-API-Key": "f4GVD94FFVQ6lRdJeenZ3RWbBBMQMcyXUFDyRlZ2"
	     })
	result = json.loads(connection.getresponse().read())
	return result

#check if current chapter is latest chapter
def compareLatestChapter(mTitle, mNewChapter, index):
	result = parseGetManga(mTitle)
	manga = result.get("results")
	
	if manga != []:
		parseLatestChapter = manga[0].get("LatestChapter")
		if mNewChapter != parseLatestChapter:
			mObjectId = manga[0].get("objectId")
			mNewDate = getDate()
			parseUpdateManga(mNewChapter, mNewDate, mObjectId, mTitle)
			writeLog("\n"+getDate()+" - UPDATED manga: "+mTitle+"("+parseLatestChapter+"->"+mNewChapter+")")
			print "\tUPDATED manga: "+mTitle+"("+parseLatestChapter+"->"+mNewChapter+")"
	else:
		print "\tADDED new manga: "+mTitle
		writeLog("\n"+getDate()+" - ADDED new manga: "+ mTitle)
		parseAddManga(index)

def writeLog(msg):
	with open('MangaLog.log', 'a') as file:
		file.write(msg)

def scrapeLatestUpdates():
	print 'Starting Scrape'
	size = len(rows)
	for i in range(0, size):
		mTitle = getTitle(i)
		mTitle = mTitle.encode('utf-8')
		try:
			compareLatestChapter(mTitle, getLatestChapter(i), i)
		except Exception:
			writeLog("\n"+getDate()+" - ERROR updating/adding: "+mTitle+"("+getLatestChapter(i)+")")
			print "\tERROR updating/adding: "+mTitle+"("+getLatestChapter(i)+")"
			continue
	print 'End of Scrape'

scrapeLatestUpdates()


