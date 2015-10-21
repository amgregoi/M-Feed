import json,httplib,urllib,requests,threading
from BeautifulSoup import BeautifulSoup

#query to obtain objects from database
#change depending on what you need
def parseGetObjects(section, limit, skip):
	connection = httplib.HTTPSConnection('api.parse.com', 443)
	params = urllib.urlencode({"where":json.dumps({
	       'Section':section,
               'MangaPic':None
	     }),'limit':limit,'skip':skip, 'order':'createdAt'})
	connection.connect()
	connection.request('GET', '/1/classes/Manga?%s' % params, '', {
	       "X-Parse-Application-Id": "gr2JB7mjsp4PhtSP0fiiS02XMhQBzO6g3siQJ5nS",
	       "X-Parse-REST-API-Key": "f4GVD94FFVQ6lRdJeenZ3RWbBBMQMcyXUFDyRlZ2"
	     })
	result = json.loads(connection.getresponse().read())
	return result


#query that makes the actual update to parse.com class
def parseUpdateChapPic(Chapter, Pic, objId):
	connection = httplib.HTTPSConnection('api.parse.com', 443)
	connection.connect()
	connection.request('PUT', '/1/classes/Manga/'+objId, json.dumps({
	       "MangaPic":Pic,
               "LatestChapter":Chapter
	     }), {
	       "X-Parse-Application-Id": "gr2JB7mjsp4PhtSP0fiiS02XMhQBzO6g3siQJ5nS",
	       "X-Parse-REST-API-Key": "f4GVD94FFVQ6lRdJeenZ3RWbBBMQMcyXUFDyRlZ2",
	       "Content-Type": "application/json"
	     })
	result = json.loads(connection.getresponse().read())
	#print result


#function that obtains the chapter number and picture url to be updated
def findChapPic(data, size):
	for i in range(0, size):
		try:
			mObjId = data[i].get("objectId")
			pResponse = requests.get(data[i].get("MangaUrl"));
			pHtml = pResponse.content
			pSoup = BeautifulSoup(pHtml)

			#finds list of chapters, pRows[0] will be the latest chapter
			pTable = pSoup.find('ul', attrs={'class' : 'chp_lst'})
			pRows = pTable.findAll('span', attrs={'class' : 'val'})

	
			#pTitle is used to remove manga title from desired chapter div
			pTable = pSoup.find('div', attrs={'class' : 'widget-title'})
			pTitle = pTable.find('h1').text.replace(" ", "")
	
			#gets pic
			pTable = pSoup.find('div', attrs={'class' : 'wpm_pag mng_det'})
			mPic = pTable.find('img')["src"]

			#Some manga a new, and have no current releases
			mChapter = "None"
			if pRows:
				rowSplit = pRows[0].text.replace(" ", "").split(":")
				mChapter = rowSplit[0].replace(pTitle, "")
			if i%50 == 0:
				print "("+str(i)+") "+data[i].get("MangaTitle")
			parseUpdateChapPic(mChapter, mPic, mObjId)
		except Exception,msg:
			print "Error in chap/pic: %s" % msg
			i = i - 1

#update chapter and pic url
def updateChapPic(sect):
	print 'Starting section: ' + sect
	while 1:
		result = parseGetObjects(sect, 1000, 0)
		data = result.get("results")
		if data == []:
			print "\t" + sect + ' is already complete'
			break
		size = len(data)
		findChapPic(data, size)

	print 'Finished section: ' + sect

if __name__ == '__main__':
	sect = "A"
	end = "Z"

	while sect <= end:
		t = threading.Thread(target=updateChapPic, args=(sect,))
		t.start()
		sect = (chr(ord(sect)+1))
	




