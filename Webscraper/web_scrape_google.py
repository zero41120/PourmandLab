from bs4 import BeautifulSoup as soup
import pprint, os, re, requests

# This function gets the content and caches it in the working directory
def get_site_content(cache_filename, url):
	if not os.path.exists(filename):
		html_content = requests.get(url).text
		with open(filename, "w", encoding='UTF-8') as f:
			f.write(html_content)
	content = open(filename, "r", encoding='UTF-8').read()
	dom = soup(content, "html.parser")
	with open(filename, "w", encoding='UTF-8') as f:
		f.write(dom.prettify())		
	return dom


##
# Publications and patent from Google scholar
filename = "google_pourmand.html"
site = "" # Manually downloaded since nasty Javascript

# Open site and parse DOM object
dom = get_site_content(filename, site)
trtags = dom.findAll('tr', {'class': 'gsc_a_tr'})

# Scrape data from html for publication 
goo_objs = []
goo_obj = { 'title': None, 'people': None, 'time': None, 'link': None, 'type': None}
for i, tr in enumerate(trtags):
	try:
		goo_obj['title']  = tr.find('a').text
		goo_obj['people'] = tr.findAll('div', {'class': 'gs_gray'})[0].text
		goo_obj['type']   = 'patent' if 'atent' in tr.findAll('div', {'class': 'gs_gray'})[1].text else 'publication'
		goo_obj['time']   = tr.find('span').text
		goo_obj['link']   = 'https://scholar.google.com' + tr.find('a')['data-href']

		if not None in [goo_obj['title'], goo_obj['people'],goo_obj['time'],goo_obj['type']]:
		 	goo_objs.append(goo_obj)
		 	goo_obj = { 'title': None, 'people': None, 'time': None, 'link': None, 'type': None}
	except Exception as e:
		pass


# Format information from data
goo_objs = [item for item in goo_objs if re.search('([0-9]{4})', item['time'])]
for obj in goo_objs:
	obj['title'] = obj['title'].lstrip().rstrip()
	obj['people'] =obj['people'].lstrip().rstrip()
	obj['time'] = obj['time'].lstrip().rstrip()
	obj['link'] = obj['link'].lstrip().rstrip()
	m = re.search('([0-9]{4})', obj['time'])
	if m:
		obj['time'] = m.group(1)



# Print information
for obj in goo_objs:
	print(obj['title'][0:50])
	print('\t' + obj['people'][0:50])
	print('\t' + obj['time'][0:50])
	print('\t' + obj['type'])
	print('\t' + obj['link'][0:50])
print('Parsed',len(goo_objs), 'objects')

start_time = 2019
def generate_markdown(obj):
	global start_time
	m = ''
	if start_time != int(obj['time']):
		if int(obj['time']) < 20119 and int(obj['time']) > 1997:
			start_time -= 1 
			m += '<h1>' +str(start_time)+ '</h1>\n'
			m += '---\n\n'

	m += '* '
	m += '<h2>[' + obj['title'] + '](' + obj['link'] + ')</h2>\n'
	m += '<p>*' + obj['people'] + '*</p>\n'
	return m

with open('patent.markdown', 'w', encoding='UTF-8') as f:
	for obj in [i for i in goo_objs if i['type'] == 'patent']:
		f.write(generate_markdown(obj) + '\n\n')

start_time = 2019
with open('publication.markdown', 'w', encoding='UTF-8') as f:
	for obj in [i for i in goo_objs if i['type'] != 'patent']:
			f.write(generate_markdown(obj) + '\n\n')

		
