import csv,time, string, re
import json,httplib,urllib
import requests
from BeautifulSoup import BeautifulSoup
import logging

#Setup
logging.basicConfig(filename='example.log',level=logging.DEBUG)
url = 'http://www.manga-joy.com/manga-list-all/'
response = requests.get(url);
html = response.content
soup = BeautifulSoup(html)

sect = "A"
end = "Z"
	
#adds from library to database
def parseAddLibrary(mTitle, mUrl, mChapter, mPic):
	#test to see if this fixes/takes care of socket errors
	checkResult = ""
	try:
		checkResult = checkIfExists(mTitle)
	except Exception,msg:
		print "Error in checkIfExists: %s" % msg
		checkResult = checkIfExists(mTitle)

	if checkResult == "false":
		connection = httplib.HTTPSConnection('api.parse.com', 443)
		connection.connect()
		connection.request('POST', '/1/classes/Manga', json.dumps({
		       "PictureUrl": None,
		       "MangaTitle": mTitle,
		       "LatestChapter": None,
		       "DateUpdated" : 'No recent updates',
		       "MangaUrl" : mUrl, 
		       "Section" :sect		#change Section with nav_apb_ctt apb_--
		     }), {
		       "X-Parse-Application-Id": "gr2JB7mjsp4PhtSP0fiiS02XMhQBzO6g3siQJ5nS",
		       "X-Parse-REST-API-Key": "f4GVD94FFVQ6lRdJeenZ3RWbBBMQMcyXUFDyRlZ2",
		       "Content-Type": "application/json"
		     })
		results = json.loads(connection.getresponse().read())
		#connection.close()
		print results
	else:
		print "already exists: "+mTitle
		pass

def checkIfExists(title):
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
	if result == {u'results': []}:
		return "false"
	else:
		return "true"

#grabs the picture for the given Manga URL
def getMangaPic(mUrl):
	pResponse = requests.get(mUrl);
	pHtml = pResponse.content
	pSoup = BeautifulSoup(pHtml)

	pTable = pSoup.find('div', attrs={'class' : 'wpm_pag mng_det'})
	mPic = pTable.find('img')["src"]
	
	return mPic

#Grabs Last update chapter for the given Manga URL
#could simplify by parsing url to chapter and getting /15/ at the end of url
def getLatestChapter(mUrl):
	pResponse = requests.get(mUrl);
	pHtml = pResponse.content
	pSoup = BeautifulSoup(pHtml)

	pTable = pSoup.find('ul', attrs={'class' : 'chp_lst'})
	pRows = pTable.findAll('span', attrs={'class' : 'val'})

	#pTitle is used to remove manga title from desired chapter div
	pTable2 = pSoup.find('div', attrs={'class' : 'widget-title'})
	pTitle = pTable2.find('h1').text.replace(" ", "")

	rowSplit = pRows[0].text.replace(" ", "").split(":")
	mChapter = rowSplit[0].replace(pTitle, "")
	return mChapter
	
def pullLibrary():
	global sect
	start = 0
	while sect <= end:
		table = soup.find('div', attrs={'class' : 'nav_apb_ctt apb_'+sect})
		rows = table.findAll('a')
		size = len(rows)
		print "("+str(size)+")"
		#for row in rows:
		for i in range(start, size):
			mTitle = rows[i].get('title')
			#mTitle = re.sub('[^a-zA-Z0-9 \n]', '', mTitle)
			link = rows[i].get('href')

			try:
				parseAddLibrary(mTitle, link, None, None)
			except Exception,msg:
				print "There was an error: %s" % msg
				parseAddLibrary(mTitle, link, None, None)
				

			if i%100 == 0:
				print "("+str(i)+"): " + mTitle
		start = 0
		print "Done: "+sect
		sect = (chr(ord(sect)+1))
		

pullLibrary()

