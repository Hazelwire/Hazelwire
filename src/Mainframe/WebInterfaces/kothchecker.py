#!/usr/bin/python
from HTMLParser import HTMLParser
import urllib2, sqlite3, time

class MLStripper(HTMLParser):
    def __init__(self):
        self.reset()
        self.fed = []
    def handle_data(self, d):
        self.fed.append(d)
    def get_data(self):
        return ''.join(self.fed)

def strip_tags(html):
    s = MLStripper()
    s.feed(html)
    return s.get_data()

def read_team_from_koth_page(url):
    page = urllib2.urlopen(url).readlines()
    for line in page:
	if line.find('SYSCORE') > 0:
            teamline = line
    if not teamline:
	return False
    team = strip_tags(''.join(teamline.split('-')[1:]).strip())
    return team.strip(' ')

def give_team_score(team):
    db = sqlite3.connect('Hazelwire.sqlite')
    c = db.cursor()
    c.execute("insert into scores values ( (SELECT id FROM teams WHERE name=?), 0, 'KOTH', ?, 1, 0);", [team, int(time.time())])
    db.commit()
    c.close()
    db.close()

if __name__ == "__main__":
    KOTH_IP = "10.1.1.137"
    team = read_team_from_koth_page('http://'+KOTH_IP)
    if team != "DELTAGREEN":
	give_team_score(team)
