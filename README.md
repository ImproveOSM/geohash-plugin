# README #

Geohash JOSM plug-in documentation.

### About ###

* Plug-in to draw a geohash grid over the JOSM map and allow search for a specific geohash. 
* Version 1.0

### Current functionalities ###

* Draws geohash grid over the JOSM map layers. 
* Using zoom map while pressing shift key will result in calculating a new geohash depth level for the geohash containing the mouse at that moment.
* Double clicking a geohash will result in removing it and the other equally sized geohashes from it's parent. This does not propagate to inner children of these geohashes.
* In the Geohash dialog there is a search option that will move the map view on the requested geohash, if input value is valid. 

### How to set up ###

* Check out the project
* Run ant build: dist and install profiles
* Open JOSM and add Geohash plugin

### External links ###

* Spaces Documentation: http://spaces.telenav.com:8080/display/TNOSM/GEOHASH+Plug-in+++-+++Product+Documentation

