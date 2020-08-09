'''
July 2020

Used to crawl the discussion forums of https://www.doctorslounge.com

@author: David Hasani
'''

#imports
from bs4 import BeautifulSoup
from urllib.request import Request, urlopen
from urllib import request
import bs4
import re
import urllib
import urllib.request
import os
import math
import sys
import io
import json
from settings import *
from BlogPost import *
import os
import smtplib
from smtplib import SMTPException
from urllib.error import HTTPError
from urllib.error import URLError
from urllib.request import urlretrieve
from datetime import datetime
import fnmatch
from json import JSONEncoder
from json import JSONEncoder
from constant import *
from PostPic import *

#write the data in JSON
def write_json(data:list, filename:str, typ:str) -> None:
    with open(filename, typ) as f:
        json.dump(data, f, indent=2, cls=CustomEncoder)

#connects to website
def make_soup(url):
    cli = request.urlopen(url)
    page = cli.read()
    soup = BeautifulSoup(page, 'html.parser')
    return soup

#remove empty values
def clean_up(lst):
    while '' in lst:
        lst.remove('')
    while ' ' in lst:
        list.remove(' ')
    while '\n' in lst:
        lst.remove('\n')

#crawl one specific page of posts
list_titles = []
list_times = []
list_profiles = []
list_text = []
list_path = []
list_url = []
list_post_url = []
list_image_url = []
list_obj = []
def crawl_post_page(url):
    soup = make_soup(url)
    all_titles = soup.find_all('h3')

    #get all titles
    for t in all_titles:
        if ('Who is ' in t.getText().strip()):
            where = t.getText().strip().index('Who is ')
            list_titles.append(t.getText().strip()[:where])
        elif (t.getText() == ''):
            list_titles.append('[no title]')
        else:
            list_titles.append(t.getText().strip())

    #get all timestamps
    all_times = soup.find_all('p', class_='author')
    for a in all_times:
        list_times.append(a.getText().strip().replace('\n', ' '))
    
    #get all profile info (ex. guest or doctor, date joined, gender, etc.)
    all_profiles = soup.find_all('dl', class_='postprofile')
    
    for p in all_profiles:
        list_profiles.append(p.getText().strip().replace('\n', ' '))
    
    #get all post text
    all_text = soup.find_all('div', class_='content')
    for t in all_text:
        list_text.append(t.getText().strip().replace('\n', ' ').replace(u'\u221a', '').replace(u'\u2212', '').replace(u'\u2265', '').replace(u'\u03b1', '') + "\n\n")

    #get all paths
    all_path = soup.find_all('span', itemprop='title')
    for c in all_path:
        list_path.append(c.getText().strip())
    
    #get all urls to the posts
    all_url = soup.find_all('a', class_='unread')
    for u in all_url:
        list_url.append('https://www.doctorslounge.com/forums' + u.get('href')[1:])
    
    #get all images
    all_post_authors = soup.find_all('p', class_='author')
    for a in all_post_authors:
        content = a.find_next_sibling('div', class_='content')
        if content is not None:
            for link in content.find_all('a', class_='postlink'):
                web = link.get('href').lower()
                if ('.jpg' in web or '.jpeg' in web or '.gif' in web or '.png' in web or '.raw' in web or '.tif' in web):
                    list_image_url.append(link.get('href'))
                    href = a.find('a', class_='unread').get('href')
                    href = href[href.index('#p'):]
                    list_post_url.append(href)
                    obj = PostPic(post_num=href, pic_url=link.get('href'))
                    list_obj.append(obj)
            for link in content.find_all('img', class_='postimage'):
                list_image_url.append(link.get('src'))
                href = a.find('a', class_='unread').get('href')
                href = href[href.index('#p'):]
                list_post_url.append(href)
                obj = PostPic(post_num=href, pic_url=link.get('src'))
                list_obj.append(obj)
            
    #discard any meaningless values
    clean_up(list_titles)
    clean_up(list_times)
    clean_up(list_profiles)
    clean_up(list_text)
    clean_up(list_path)
    clean_up(list_url)
    clean_up(list_post_url)
    clean_up(list_image_url)
    
#check if there are multiple pages of posts within this webpage
def check_num_pages(url):
    soup = make_soup(url)
    num_posts = int(soup.find('div', class_='pagination').getText().strip()[:2].strip()) #extracts number of posts, if > 15 then at least 2 pages of posts are present
    num_pages = math.ceil(num_posts/15)
    
    if (num_pages > 1):
        start = 15
        for x in range(num_pages-1):
            crawl_post_page(url + '&start=' + str(start))
            start += 15 #each successive page (posts) adds a multiple of 15 to end of url

#crawl entire topic (consisting of at least 1 page of posts)
def crawl_one_topic(url):
    crawl_post_page(url)
    check_num_pages(url)
    
#check if there are multiples pages within this index
def check_num_pages_in_index(url):
    soup = make_soup(url)
    space_loc = soup.find('div', class_='pagination').getText().strip().index(' ')
    num_topics = int(soup.find('div', class_='pagination').getText().strip()[:space_loc].strip()) #extracts number of pages, if > 100 then at least 2 pages of topics are present
    num_pages = math.ceil(num_topics/100)
    
    if (num_pages > 1):
        start = 100
        for x in range(num_pages-1):
            crawl_one_index(url + '&start=' + str(start))
            start += 100 #each successive page (indices) adds a multiple of 100 to end of url
    
#crawl ONE PAGE of one index of website (ex. 'Surgery Topics', 'Renal Failure', 'Diabetes', etc.); there are 96 total indices of the website
def crawl_one_index(url):
    soup = make_soup(url)
    all_topic_links = soup.find_all('a', class_='topictitle')
    
    for a in all_topic_links:
        a2 = a.getText()
        if 'Low Progesterone Cause of Miscarriage' in a2: #cannot access this topic (locked on site), so remove it
            all_topic_links.remove(a)
    
    list_topics = []
    for t in all_topic_links:
        list_topics.append('https://www.doctorslounge.com/forums' + t.get('href')[1:])
    
    for l in list_topics:
        crawl_one_topic(l)

#crawls an entire index (all pages)
def crawl_entire_index(url):
    crawl_one_index(url)    
    check_num_pages_in_index(url)
    
#crawl entire website
def crawl_all(url):
    soup = make_soup(url)
    list_forums = soup.find_all('a', class_='forumtitle')
    for u in list_forums:
        link = 'http://www.doctorslounge.com/forums' + u.get('href')[1:]
        crawl_entire_index(link)

#crawl one page of the authors list
list_usernames = []
list_prof_links = []
list_ranks = []
list_num_posts = []
list_joined_dates = []
temp_list=[]
def crawl_page_authors(authors_link, start):
    soup = make_soup(authors_link)
    
    #extract usernames
    for a in soup.find_all('a'):
        if a.has_attr('href'):
            if 'viewprofile' in a.get('href'):
                list_usernames.append(a.getText())
                list_prof_links.append(a.get('href')[1:])
    
    #extract ranks
    for r in soup.find_all('span', class_='rank-img'):
        rank = r.getText().strip()
        if (rank == ''):
            rank = '[no rank]'
        list_ranks.append(rank)
    list_ranks.pop(start)
    
    #extract number of posts
    for p in soup.find_all('td', class_='posts'):
        list_num_posts.append(p.getText())
    
    #extract joined dates
    global temp_list
    for d in soup.find_all('td'):
        temp_list.append(d.getText()[4:])  
    global list_joined_dates
    temp_list = temp_list[3::4]
    for t in temp_list:
        list_joined_dates.append(t)
    temp_list.clear()

#crawl the entire index of authors
def crawl_authors(url):
    crawl_page_authors(url, 0)
    start = 100
    for x in range(661):
        crawl_page_authors(url + '?&start=' + str(start), start)
        start += 100

#crawl each profile page of the authors
list_genders = []
list_ages = []
list_jobs = []
list_locs = []
list_emails = []
def crawl_profs(url):
    soup = make_soup(url)
    info = soup.find('dl', class_='left-box details profile-details').getText()
    
    gender = '[no gender]'
    if 'Gender:' in info:
        gender = info[info.index('Gender:')+8:info.index('Gender:')+9].strip()
    list_genders.append(gender)
    
    age = '[no age]'
    if 'Age:' in info:
        age = info[info.index('Age:')+5:info.index('Age:')+7].strip()
    list_ages.append(age)
    
    job = '[no job]'
    if 'Occupation:' in info:
        if 'Location:' in info:
            job = info[info.index('Occupation:')+12:info.index('Location:')].strip()
        else:
            job = info[info.index('Occupation:')+12:].strip()
    list_jobs.append(job)
    
    loc = '[no location]'
    if 'Location:' in info:
        loc = info[info.index('Location:')+10:].strip()
    list_locs.append(loc)
    
    contact = soup.find('div', class_='column1').getText()
    email = '[no email]'
    if 'AOL:' in contact:
        email = 'AOL: ' + contact[contact.index('AOL:')+5:].strip()
    list_emails.append(email)
        
#put together the complete list of author information
def assemble_authors():
    auth_list = []
    for x in range(len(list_usernames)):
        a = Author(name=list_usernames[x], prof_link=list_prof_links[x], rank=list_ranks[x], num_posts=list_num_posts[x], joined_date=list_joined_dates[x], 
                                                    gender=list_genders[x], age=list_ages[x], job=list_jobs[x], location=list_locs[x], email=list_emails[x])    
        auth_list.append(a)
    
    return auth_list 

#create list of BlogPost objects
def assemble_blog_posts():
    obj_list = []
    x = 0
    for t in list_times:
        b = BlogPost(author=list_profiles[x], title=list_titles[x], content=list_text[x], posted_time=list_times[x], category=list_cat[x], 
                                                                                            sub_category=list_subcat[x], url = list_url[x])
        obj_list.append(b)
        x += 1
        
    return obj_list

#save the pictures
def save_pics(file):
    f = open(file)
    data = json.load(f)
    for d in data:
        url = d['pic_url']
        if url[-4:].lower() == '.jpg' or url[-4:].lower() == 'jpeg' or url[-4:].lower() == '.gif' or url[-4:].lower() == '.bmp' or url[-4:].lower() == '.png':
            try:
                print(urlretrieve(url, d['post_num']))
            except FileNotFoundError as err:
                print(err)   
            except HTTPError as err:
                print(err)  
            except URLError as err:
                print(err)
            except ValueError as err:
                print(err)

#combines lists of data
def combine_lists(list1, list2):
    master_list = [i + j for i, j in zip(list1, list2)]
    return master_list
        
#start the crawling
if __name__ == '__main__':
    
    crawl_all('https://www.doctorslounge.com/forums/index.php')
    
    #extract category and subcategory
    cat = list_path[2]
    subcat = list_path[3]
    
    list_cat = []
    list_subcat = []
    
    #populate lists with correct number of elements
    while not (len(list_cat) == len(list_url)):
        list_cat.append(cat)
        list_subcat.append(subcat)
    
    #remove unneeded polls in some posts
    for a in list_text:
        if('Your vote has been cast' in a):
            list_text.remove(a)  
    for b in list_times:
        if('Poll ended at' in b):
            list_times.remove(b)
            
    #combine lists and write data
    write_json(assemble_blog_posts(), '/Users/david/Desktop/Privacy/' + subcat + '.json', 'w')
    
'''
    #Comment / Un-Comment lines as needed to perform tasks

    #map the posts to the pictures
    write_json(list_obj, '/Users/david/Desktop/Privacy/Post_To_Picture_Mapping.json', 'w')

    #save pictures to Desktop
    os.chdir('C:/Users/david/Desktop/Privacy/Pictures')
    save_pics('C:/Users/david/Desktop/Privacy/Post_To_Picture_Mapping.json')

    #get all user info
    url = 'https://www.doctorslounge.com/forums/memberlist.php'
    crawl_authors(url)
    
    print(len(list_usernames))
    print(len(list_prof_links))
    print(len(list_ranks))
    print(len(list_num_posts))
    print(len(list_joined_dates))
    
    print('\nMORE INFO:\n')
    
    for u in list_prof_links:
        crawl_profs('https://www.doctorslounge.com/forums' + u)
    
    print(len(list_genders))
    print(len(list_ages))
    print(len(list_jobs))
    print(len(list_locs))
    print(len(list_emails))
    
    write_json(assemble_authors(), '/Users/david/Desktop/Privacy/UserInfo.json', 'w')
                    
    print('\nCategory:    ' + str(len(list_cat)))
    print('Subcategory: ' + str(len(list_subcat)))
    print('URL:         ' + str(len(list_url)))
    print('Author:      ' + str(len(list_profiles)))
    print('Title:       ' + str(len(list_titles)))
    print('Time:        ' + str(len(list_times)))
    print('Post:        ' + str(len(list_text)))
'''
