# Meeting Monday 14th March 

- Time: 16h30 - 17h45
- Participants: all
- Topic: Plan the week's sprint

## Fixing some scaling issues

- estimate question screen MR was still a draft because of a small resize bug (columns not scaling as intended)
- fixed this as a team, set never resize on horizontal property of joker columns
- looks much nicer now, but still need to finish scaling individual elements and text

## Aims

- get activities and figure out a way to put them on the server
- making client side for game, interpreting messages (correct answer, question, leaderboard, players, end)
- have game create scenes/questions
- controller to interpret the messages and take actions accordingly (uses server utils)
- finish fixing the fxml resizing (have the buttons and text scale as well else not clean, put a limit on minimum screen size)
- clean up fxml and make it production quality
- client/server communication and methods (join, poll, submit, leave) -> ServerUtils

## Distribution

- 1 person on the server utils functions: Juan
- 1 person on finishing the jokers: Maarten
- 1 person on fxml: Tudor
- 3 people on the controller: Kayleigh, Ana, Wiktor

## Additional discussion

- synchronized method vs volatile on the stage queue to fix some concurrency problems in the "ifStageEnded()" method
- if this turns out to be an actual issue we will probably make it synchronized, until then we'll see
