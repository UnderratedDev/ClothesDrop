Getting Started

Built for API 15+

These instructions will get you a version the project up and running on your local machine for deployment/further development and testing purposes.

Prerequisites

- Android Studio (Any version, may require a lot of updating)
- Installation of any JDK (Java SE Development Kit) that is compatiable with the version of android studio you are running,
  preferably something new

Installing

-- Setup for Development/Deployment --

1) Copy the ClothesDrop folder (the entire folder, not just the contents) to a location you like
2) Open Android Studio, on first launch, there should be a small window
3) Select something along the lines of open existing project
4) It will open a small window full of directories. Locate where the clothesdrop folder is stored, and select that
   -- Note do not select inside the folder, just select the folder itself --
5) Now Android Studio will start building the project/gradle
6) You should now be setup to develop further!!! or even deploy!

-- Installing on Phone --
    -- Using APK in root folder --
    1) Copy pase the apk-debug.apk to your android device
    2) Go to your android device and locate the file using a file manager (file commander can be found on the play store)
    3) To enabled developer options : http://blog.syncios.com/enable-developer-optionsusb-debugging-mode-on-devices-with-android-4-2-jelly-bean/
    4) Click on it and run it, it may popup with installation blocked etc, if so go to settings, security (if old device),
       or developer options if newer device and check unknown sources
    5) Now install it and run it like a regular app
    6) Here is a link to a similar guide : https://www.cnet.com/how-to/how-to-install-apps-outside-of-google-play/
    
    -- Using Android Studio --
    1) Open the project
    2) Plug in your android phone (can use emulator but it slower)
    3) Enabled adb debugging on android device : https://www.kingoapp.com/root-tutorials/how-to-enable-usb-debugging-mode-on-android.htm
    4) Select the green play button
    5) It will open a window, select your device on the list and then wait for it to deploy on your phone
    6) It should now be on your phone!! It will launch automatically
    
-- Generating an Debug APK --
1) Go to Android Studio
2) Select Build -> Build APK
3) Once complete it should, popup in the bottom right stating open in explorer, see above to transfer apk to android device
4) See below in Deployment on how to generate release apk!
  
-- Deployment --
These links should guide you to deployment
https://developer.android.com/distribute/best-practices/launch/launch-checklist.html
https://developer.android.com/distribute/best-practices/launch/index.html
https://developer.android.com/studio/publish/index.html

Calendar and Region
-- Events on Calendar --
The calendar that is being using is Google Calendar.
The Account that is being used is the one hemant provided us.
To specify if a pickup date is not on the calendar, in the google calendar associated with the google account
named myCalendar (this is the calendar the dates are being pulled from), add an event on the day
that is invalid and the backend will every 15 mins update the pickup dates for the user.
In 15 mins the app user will not be able to see the day that has the event on it.

-- Region Changes --
In the admin dashboard, if a change is made to the user pickup limit or the days the region can pickup,
then the dates will be updated in 15 mins when the service runs. The user will see the change in 15 mins.

Running the tests
Inside the project/app/java/nestedternary.project(androidTest) folder, open each java test file and run it
These java files test the app by opening it and calling the methods using reflection
These are functional tests

Built With
Android Studio 2.3.1            - The development tool
Java 8                          - The programming language
GreenDao 3.2.2                  - Used to generate the database
mySQL 5.7.17                    - The programming language
JUnit4                          - Unit testing framework
Google Play Services 6.5.87     - To use Google Play Services 
Google Maps API 10.0.1          - For Google Maps functionality

Versioning
1.0 - Contains all features requested for the app and all have been tested :)

Authors
Yudhvir Raj   - Developer / Tester
Joanne  Hsu   - Developer / Tester
Stephen Cheng - Developer / Tester
David   Yu    - Developer / Tester