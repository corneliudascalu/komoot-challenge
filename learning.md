First of all, thank you for this challenge. Really. Working on it was fun, and the result is something actually interesting enough to show to my friends. 

My initial reaction was "this is easy". I had the experience of a recent interview challenge at another company which asked to create a full-fledged weather app with Firebase sign up and login. It took me more than a weekend and still didn't manage to finish everything.
My second reaction was "this is going to be interesting". I didn't anticipate any real hurdles, but there may be some gotchas: retrieving the location in the background, or dealing with potentially hundreds of photos cached in memory.
I quickly checked a few essential things:

- that the FusedLocationProvider actually has an API for getting location updates every 100m. LocationRequest.setSmallestDisplacement(). Nice.
- that Flickr offers an easy to use API to search photos based on location. It does, but I need an API key, so I requested one right away, expecting a delay until it is granted. Surprisingly, it was instant.

I went to bed and lost some sleep planning the solution. I finally fell asleep after I decided to fake the location provider and tackle the Flickr API first, so I could build the UI first, handle any issues with the API (deserialization, network errors, edge cases like "no photos found"). After I had a list of photos updating correctly, I could jump into the hard part: location permissions, keeping a foreground service alive and, last but not least, actually going outside to test the final version.

---

It took me a couple of hours setting up the UI. I started with a simple recycler view displaying strings in textviews, but still, I got stuck on stupid stuff like nothing being displayed because I forgot to implement the bind() method of the ViewHolder. Anyway, I managed to load random photos from picsum.photos every second, and then implemented the Flickr search request using Retrofit.

I still had the most difficult part to do, location updates and storing the list of URLs in the absence of an UI. Storing in memory should be good enough, but it feels risky.

---

I spent another couple of hours setting up the foreground service which will request the location updates. I managed to make it work as I wanted: started when the user starts walking, bound to the activity (and unbound in onStop()), and stopped when the user stops the walk with the Stop button. I had some complications managing the state of the service against the state of the MainActivity. In the end I managed to fix them by letting the `locationService` be nullable, and set it null when it is unbound, so the activity doesn't try to start it again until it explicitly binds to it.

---

I spent some time in the evening setting up the location request, fused location provider client etc. No major problem getting the location updates. With a little effort researching coroutines, I managed to set up a flow of location updates and map them into FlickrPhotos. Pretty nice. 
This morning I managed to hook everything together and make the photos flow available in the viewmodel, while also storing the photos in the repository. There are some rough edges: I haven't tested enough what happens when the activity is destroyed while the service is running, or when the user taps the foreground notification etc.

-------

I feel like I could spend several days on it until I get it polished enough to be proud of it. There are many things missing: no unit tests, no local storage, no error handling and so on. It does what is required, but I still have the feeling that all I'm proving with this challenge is that I can read documentation. I didn't really have time to build a nice architecture or clean up the code to make it readable and maintainable.




