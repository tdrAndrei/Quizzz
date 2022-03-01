## Team meeting week 4 - notes
Date:           01-03-2022\
Main focus:     Feedback HCI & Discussion on planning the game\
Chair:          Kayleigh Jones\
Note taker:     Ana Bătrîneanu

## Meetings in the past week
### Thursday 24-02-22 from 16:15 to ~ 20:50: 
- HCI rough draft made in shared drive,
- live coding (endpoints) in smaller groups
- checkstyle decided & merged
- wrote questions for teamwork Q&A

### Monday 28-2-22, 16:00 - ?
  
- client-server communication
- client closes: drop his name from the table
- unique id to be kept
- check uniqueness on server on first screen
- enforce uniqueness at login time ( + later on, take a look at this option; maybe check for uniqueness when choosing multiplayer..)
- leaderboard = all time for all users
- different name ONLY FOR CURRENT players
- waiting room does not have to be persistent; don't need db for that

USER 
- connect : insert in table
- disconnect :  delete in table

GAME class
- keep a list of players which are playing in a game
- 1 SCENE for GAME (all questions)

LEADERBOARD - DB - single player
- USER object (FK) + score
- sort on score
- pair <user, score>

PLAYER : - store user + score
- Player extends User


- 1 person JFX                              
2 game logic 
1 person update serverutils               
1 person player, entity, question

logic screen + main screen (check database)

### Monday 28-2-22, 20:30-23:00
- Login stuff

### Tuesday 14:30-16:30
- plan classes etc

# Opening
All team members were present physically

# Agenda points
- Report what we did in the meetings
- HCI feedback
- Do we need the final UI designs by friday? Are we still allowed to change designs after this?
- What's next? (scenes?)
- Large commits
- Long polling vs normal during game
- How to format questions? Activities vs specific, randomisation?

# Points of action
## Deadlines:
this week (until Friday 4th):
- teamwork A
- HCI final document
- upload questions in activity bank

next week:
- Buddy check

## HCI Feedback
- Intro: describe the project more (screens, buttons) + include the images here
- Mention how the experts had to evaluate the design (how many times they went on it, did they grade it individually or in group) + elaborate on the process
- Conclusion + Improvements : include the improved images + explain why we chose to improve what we did (tie with actual measurements; what did we prioritize?)
- Results section: for each problem use the matrix/measurement to assess it (how bad it is; frequency impact)
- 1 person confused : we can say it's a design choice;

## General
- start testing - maybe have 1 person test per week; once making a class test it
- JavaDoc plugin (write more elaborate descriptions for more complicated methods)
- don't squash the commits unless necessary
- we can still change our designs after HCI final deadline
- more frequent&smaller commits
- no preference between long polling and regular polling
- make sure we don't run out of memory

# Closing
- We have currently split our work and we will meet on Thursday to discuss everybody's progress.