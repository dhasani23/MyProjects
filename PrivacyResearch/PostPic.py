#class to map the post number to the image(s) found within
class PostPic:
    post_num = -1
    pic_url = ""
    def __init__(self, post_num, pic_url):
        self.post_num = post_num
        self.pic_url = pic_url
