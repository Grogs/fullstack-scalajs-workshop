# Exercise 3
Now we're going to allow the user to display all the conferences on a map.
  
Steps:

1. Add a modal and a button to open it, in which we will display the map. 
    * Documentation: 'Modal card' section of https://bulma.io/documentation/components/modal/
    * Fix the new failing tests in `EventsControllerTest`.

2. Add an empty Map inside the modal.
    * Put the map related code in the `me.gregd.scalax.GoogleMaps.render` which runs every time the user opens the Map.
    * See https://developers.google.com/maps/documentation/javascript/examples/map-simple
    * `render` has the element you should render the map to as a parameter.
    * NOTE: In the docs a JavaScript object literal is used when creating the `Map`, and the center's latitude and logitude.
      But we don't pass object literals from ScalaJS if we can avoid it. We want type safety! 
      So, instead, you will have to pass a instance of `MapOptions` to `Map` and create a `LatLng` for the center.

3. Add a Marker for each conference. 
    * See https://developers.google.com/maps/documentation/javascript/examples/marker-simple
    * NOTE: Again, like the Map, we won't use an object literal to configure the marker, use `MarkerOptions` instead.

4. Update the Map's LatLngBounds, so the Map focuses on the right area. 
    * See https://coderwall.com/p/hojgtq/auto-center-and-auto-zoom-a-google-map

5. Add InfoWindows to display each conference's description when a marker is clicked on. 
    * See https://developers.google.com/maps/documentation/javascript/examples/infowindow-simple







# Exercise 2
Let's add some interactivity to the page and let the user see past conferences.

Start by adding a dropdown. Re-run `EventsControllerTest` and fix the new failing tests.

Then, we move to ScalaJS! But I haven't written any tests for that...

So, please start the app by executing:
`sbt run`

Then browse to [http://localhost:9000](http://localhost:9000) where you should have a listing of the upcoming conferences, and you dropdown which doesn't do anything yet.

Now go to `me.gregd.scalax.App` in the client and update the main method (which runs on page load) to go through these steps:

1. Start by adding an event listener to the newly added select element.
    * Take a look through all the events available and decide which one to use: https://www.w3schools.com/jsref/dom_obj_event.asp
    * Then you can use the `addEventListener` method on an element to add a new listener.
    * add a println, reload the page and verify that it works as you expect.
2. When this event gets triggered we want to fetch all the conferences matching that query and re-render the page:
    * You can fetch the new conferences from the server using the Autowire `Client`
        * Use `Client[ConferenceService]` to call methods on the ConferenceService interface that's defined in the shared module but implemented in the server. 
        * See the Autowire documentation: https://github.com/lihaoyi/autowire#minimal-example
        * NOTE: You need to add `.call()` to the invocation to Client 
    * Generate the new conference cards HTML using the `view.eventsTable` template from exercise 1.
    * Replace the old conference listings HTML with the HTML. (Hint: `.innerHTML`)
    * Refresh the page and check it works
    
    
    
    
    

# Exercise 1

Let's start by fleshing out the event listings. Currently it's a blank page; let's show the upcoming conferences that are being passed through from the `EventsController`.  

This will be server-side only and test driven. Run `EventsControllerTest` and fix the failing tests.  

You'll need to modify the server-side only template for the events page. See `eventListings.scala.html`.  
Then you'll have to build out the shared events table template. See `eventsTable.scala.html`.

Once you're done, start up the application with `sbt run`, browse to http://localhost:9000 and check that you can see the upcoming conferences.

Then move on to exercise two by checking out the `exercise2` branch.