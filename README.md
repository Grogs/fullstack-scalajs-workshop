# Exercise 1

Let's start by fleshing out the event listings. Currently it's a blank page; let's show the upcoming conferences that are being passed through from the `EventsController`.  

 
This will be server-side only and test driven. Run `EventsControllerTest` and fix the failing tests.  

You'll need to modify the server-side only template for the events page. See `eventListings.scala.html`.  
Then you'll have to build out the shared events table template. See `eventsTable.scala.html`.

Once you're done, start up the application with `sbt run`, browse to http://localhost:9000 and check that you can see the upcoming conferences.

Then move on to exercise two by checking out the `exercise2` branch.