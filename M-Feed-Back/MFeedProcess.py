#write long running process
#call mjScrapeUpdates every x interval to scrape updates and update parse
import time
import os

while 1:
	os.system("python mjScrapeUpdates.py")
	print
	print
	print "Process: Sleeping for 10 minutes"
	time.sleep(600)
	print "Process: Waking up"
