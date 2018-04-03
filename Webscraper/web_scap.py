from bs4 import BeautifulSoup as soup
import pprint, os, re, requests

# This function gets the content and caches it in the working directory
def get_site_content(cache_filename, url):
	if not os.path.exists(filename):
		html_content = requests.get(url).text
		with open(filename, "w", encoding='UTF-8') as f:
			f.write(html_content)
	return open(filename, "r", encoding='UTF-8').read()

# Site to scraper
filename = "publications.html"
site = "https://bionanotech.soe.ucsc.edu/content/publications"

# Open site and parse DOM object
html_content = get_site_content(filename, site)
dom = soup(html_content, "html.parser")
ptags = dom.find('div', {'class': 'field-item even'}).findAll('p',{})

# Scrape data from html for publication 
pub_objs = []
pub_obj = { 'title': None, 'people': None, 'time': None, 'link': None }
for p in ptags:
		link_tag = p.find('a')
		if link_tag != None:		
			pub_obj['link'] = link_tag['href']
		m = re.search('^[0-9]{1,2}\.(.+)$', p.text)
		if m != None:
			print(p.text[0:50], m.group(1)[0:50])
			pub_obj['title'] = m.group(1)
		if 'ourmand' in p.text:
			pub_obj['people'] = p.text
		if '20' in p.text or '199' in p.text:
			pub_obj['time'] = p.text
		if not None in [pub_obj['title'], pub_obj['people'],pub_obj['time'],pub_obj['link']]:
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
# for obj in pub_objs:
# 	print(obj['title'][0:100])
# 	print('\t' + obj['people'][0:50])
# 	print('\t' + obj['time'][0:50])
# 	print('\t' + obj['link'][0:50])



# Site to scraper
filename = "patents.html"
site = "https://bionanotech.soe.ucsc.edu/content/patents"

# Open site and parse DOM object
html_content = get_site_content(filename, site)
dom = soup(html_content, "html.parser")
litags = dom.find('div', {'class': 'field-item even'}).findAll('li',{})

# Scrape data from html for patents
pat_objs = []
pat_obj = { 'title': None, 'people': None, 'time': None, 'link': None }
for tag in litags:
	link_tag = tag.find('a')
	if link_tag != None:
		pat_obj['link'] = link_tag['href']
	m = re.search('(.*)["“”](.*)["“”](.*)([0-9]{4})(.*)', tag.text)
	if m:
		pat_obj['title'] = m.group(2)[:-2]
		pat_obj['people'] = m.group(1)[:-2]
		pat_obj['time'] = m.group(4)
	if not None in [pat_obj['title'], pat_obj['people'],pat_obj['time'],pat_obj['link']]:
		pat_objs.append(pat_obj)
		pat_obj = { 'title': None, 'people': None, 'time': None, 'link': None }


# for obj in sorted(pub_objs, key=lambda x: x['time']):
# 	print(obj['title'][0:100])
# 	print('\t' + obj['people'][0:50])
# 	print('\t' + obj['time'][0:50])
# 	print('\t' + obj['link'][0:50])


print(len(pub_objs))

with open('publication.markdown', 'w', encoding='UTF-8') as f:
	start_time = 2016
	for obj in pub_objs:
		m = ''
		if start_time != int(obj['time']):
			print('comparetime', start_time, int(obj['time']))
			if int(obj['time']) < 2016 and int(obj['time']) > 1997:
				start_time -= 1 
				m += '<h1>' +str(start_time)+ '</h1>\n'
				m += '---\n\n'

		m += '* '
		m += '<h2>[' + obj['title'] + '](' + obj['link'] + ')</h2>\n'
		m += '<p>*' + obj['people'] + '*</p>\n'
		f.write(m + '\n\n')


print(len(pat_objs))

with open('patent.markdown', 'w', encoding='UTF-8') as f:
	start_time = 2016
	for obj in sorted(pat_objs, key=lambda x: x['time'], reverse=True):
		m = ''
		if start_time != int(obj['time']):
				m += '<h1>' + str(obj['time']) + '</h1>\n'
				m += '---\n\n'
				start_time = int(obj['time'])

		m += '* '
		m += '<h2>[' + obj['title'] + '](' + obj['link'] + ')</h2>\n'
		m += '<p>*' + obj['people'] + '*</p>\n'
		f.write(m + '\n\n')