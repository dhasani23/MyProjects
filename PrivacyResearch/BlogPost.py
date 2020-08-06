'''
July 2020

@author: David Hasani
'''

from json import JSONEncoder
from constant import *
from ast import Num

class BlogPost:
    author = None
    title = None
    content = None
    posted_time = None
    category = None
    sub_category = None
    url = None
    #like_count = None
    #comments = []
    def __init__(self, author, title, content, posted_time, category, sub_category, url):
        self.author = author
        self.title = title
        self.content = content
        self.posted_time = posted_time
        self.category = category
        self.sub_category = sub_category
        self.url = url
        
#subclass JSONEncoder
class CustomEncoder(JSONEncoder):
        def default(self, o):
            return o.__dict__

class Author:
    name = ''
    profile_link = ''
    rank = ''
    num_posts = ''
    joined_date = ''
    age = ''
    gender = ''
    job = ''
    location = ''
    email = ''

    def __init__(self, name, prof_link, rank, num_posts, joined_date, gender, age, job, location, email):
        self.name = name
        self.prof_link = 'www.doctorslounge.com/forums' + prof_link
        self.rank = rank
        self.num_posts = num_posts
        self.joined_date = joined_date
        self.age = age
        self.gender = gender
        self.job = job
        self.location = location
        self.email = email
