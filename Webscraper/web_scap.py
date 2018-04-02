from bs4 import BeautifulSoup as soup
from urllib.request import urlopen
import pprint, os, re

# This function gets the content and caches it in the working directory
def get_site_content(cache_filename, url):
	if not os.path.exists(filename):
		site_client = urlopen(url)
		html_content = site_client.read()
		site_client.close()
		with open(filename, "w") as f:
			f.write(str(html_content))
	return open(filename, "r").read()

# Site to scraper
filename = "publications.html"
site = "https://bionanotech.soe.ucsc.edu/content/publications"

# Open site and parse DOM object
html_content = get_site_content(filename, site)
dom = soup(html_content, "html.parser")
ptags = dom.find('div', {'class': 'field-item even'}).findAll('p',{})

# Scraper data from html
pub_objs = []
pub_obj = { 'title': None, 'people': None, 'time': None, 'link': None }
for p in ptags:
		link_tag = p.find('a')
		if link_tag != None:		
			pub_obj['link'] = link_tag['href']
		m = re.search('^[0-9]{1,2}\..+$', p.text)
		if m != None:
			pub_obj['title'] = p.text
		if 'ourmand' in p.text:
			pub_obj['people'] = p.text
		if '20' in p.text or '199' in p.text:
			pub_obj['time'] = p.text
		if not None in [pub_obj['title'], pub_obj['people'],pub_obj['time'],pub_obj['link']]:
			print(pub_obj['title'][0:50])
			pub_objs.append(pub_obj)
			pub_obj = { 'title': None, 'people': None, 'time': None, 'link': None }

# Format information from data
for obj in pub_objs:
	m = re.search('^([0-9]{1,2}\.[^.]+\.)(.+)$', obj['people'])
	if m:
		obj['people'] = m.group(2)
	m = re.search('([0-9]{4})', obj['time'])
	if m:
		obj['time'] = m.group(1)

# Print information
for obj in pub_objs:
	print(obj['title'][0:100])
	print('\t' + obj['people'][0:50])
	print('\t' + obj['time'][0:50])
	print('\t' + obj['link'][0:50])


print(len(pub_objs))
		
