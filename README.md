# DropApp
An Android app designed to detect when a device is dropped.

This project is for Professor Guanling Chen's Ubiquitous Computing class of Spring 2016.

March 14-26 Progress:
  Documented project requirements
  Created github repositiory
  Mapped out features
  Starting taking accelorometer data as a service
  Display accelorometer data on the app in graph and numeric form
  
March 27-April 2 Weekly Progress:
  Modified the app to record accelorometer data
  Collected various accelorometer data
  Began designing the algorithm to detect a drop

April 3-10 Weekly progress:
  Basic implementation of the algorithm
    Implemented 2 state machines
      one that detects the drops and the other that takes action when there is a drop
  Added notifications
  Added more debug information on the app 
  Added api for saving user settings

April 11-17 weekly progress:
  Implemented correct grace period behavior
    if phone moves when checking for rest do not fire alarm
  Created logo!
  Worked on setup page
    runs when app is first loaded
      take in users email address and calibrate device
  Added ability for user to dismiss drop notification
  Starting working of presentation

April 18-24 weekly progress:
  Talked about feasibility of Android Activity
  Adjust calibration to do it multiple times
    user drops phone 3 times from waist height on soft surface
  Worked on email notification
  More work on presentation
  Worked on report
  ability for user to enter email
  Got email to send (GmailBackground library), still need coordinates
    sends email without intent (no user interaction)
  
  
  TODO
    Email notification with gps coordinates
    hook up settings to calibrate multiple times
  
