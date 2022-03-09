# Meeting Monday 7th March

- Time: 16h00 - 18h30
- Participants: all (Juan Tarazona had to leave at about 16h45 due to a medical emergency)
- Agenda: Discuss Sprint week 5

## Suggestions/Specifications ~ Server Side

- fully implement the Game classes and have them be functional
- we need some message entity (probably in commons); with type and fields for every possible type
- Game class: should have an id, a map with player ids as keys (forwarded by game controller), also date object (start time) and queue of stages 
- we also keep info about maximum time a player has to answer a question and optionally we could set a hard cap on the number of players
- in order to not send excess data to the players we keep a map with ids as keys and messages as values (the diff map, checks if data is unimportant)
- getUpdate method for the game class to check if there is a message to be sent
- in terms of methods: constructor, hardcode stage queue (all games same structure), and an initialize stage method, add player (with user as param)
- somehow check if the stage has ended and in response to that change the stage with initialize stage, finally add some joker methods as well

### Msg types (convention)

- None: all fields null
- NewPlayers: a list of all other players; other fields null
- NewQuestion: the question and add score; other fields null
- ShowLeaderboard: a list of leaderboard entries; other fields null
- EndGame: also a list of leaderboard entries
- ShowCorrectAnswer: give client the score and correct answer for the question

## Suggestions ~ Front End/Client

- work on resizing the UI properly; main screen, login, leaderboard
- resize event to scale the elements and/or using grid pane
- possibly link functionality with backend
- refactor the css structure