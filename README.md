## Description of project
This is _Quizzzz!_, a fast-paced educational quiz game geared towards raising awareness about energy usage.  
This project is powered by the [Spring](https://spring.io/) library for the database and API functionality and the [javaFX](https://openjfx.io/) library for an interactive user interface.
## Group members

| Profile Picture | Name | Email |
|---|---|---|
| ![](profile_images/AvatarTAndrei.jpg) | Tudor Andrei | T.Andrei@student.tudelft.nl |
| ![](profile_images/Webp.net-resizeimage.jpg) | Juan Tarazona | J.J.TarazonaRodriguez@student.tudelft.nl |
| ![](profile_images/picture-maarten.jpg) | Maarten van der Weide | M.V.vanderWeide@student.tudelft.nl |
| ![](profile_images/Wiktor_photo.jpg) | Wiktor Grzybko | W.J.Grzybko@student.tudelft.nl |
| ![](profile_images/pic-Ana.jpg) | Ana Bătrîneanu | A.Batrineanu@student.tudelft.nl |
| ![](profile_images/KayleighPicture.jpg) | Kayleigh Jones | K.M.Jones@student.tudelft.nl |

<!-- Instructions (remove once assignment has been completed -->
<!-- - Add (only!) your own name to the table above (use Markdown formatting) -->
<!-- - Mention your *student* email address -->
<!-- - Preferably add a recognizable photo, otherwise add your GitLab photo -->
<!-- - (please make sure the photos have the same size) --> 

## How to run it
1. Clone the project to your local machine
2. Building the project can be done in multiple ways:
    1. In your IDE of choice open the project and run the gradle.build script.
    2. In a terminal of your choice navigate to the cloned project and run ```gradle build```
3. To start the server type ```gradle run``` (to configure the server for first-time use see step 4)
4. To configure the server you need to do the following things:
    1. Start the server via the ```gradle bootRun```
    2. Let the server fully start. If it is done starting up you will see that a file named 'quizzzz.mv.db' is created
    3. Stop the server and make sure it is off for the next step.
    4. To get activities into this newly generated database you can follow the following steps
        1. Create your own activities or fetch a release from [here](https://gitlab.ewi.tudelft.nl/cse1105/2021-2022/activity-bank/-/releases)
        2. Fetch the release version of this [database injector script](https://github.com/MrMModder/dbLoader/releases/tag/WORKING) and follow the instructions to insert the activities.
4. Run a client:
    1. In a terminal run the following command ```gradle run```

## How to contribute to it
If you want to contribute to this project you can clone the project and make a merge request to development  
In the case you run into any issues you can make an issue here on gitlab with the appropriate labels.
