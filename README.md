# README #

Geohash JOSM plug-in documentation.

### About ###

* Plug-in to display a geohash grid over the JOSM map and allows the search for a specific geohash. 
* Version 1.0

### Current functionalities ###

* Displays a geohash grid over the JOSM map layers. 
* Using zoom map will result in calculating a new geohash depth level based on the current coordinates of the view port.
* Double clicking a geohash will result in removing it and the other equally sized geohashes from it's parent.
* In the Geohash dialog there is a search option that will move the map view on the requested geohash, if input value is valid. 
* The layer menu opened by right clicking the geohash layer has the option to remove all geohashes that recomputes the geohashes based on the current map view.

### How to set up ###

* Check out the project
* Run ant build: dist and install profiles
* Open JOSM and add Geohash plugin

### External links ###

* GitHub: https://github.com/ImproveOSM/geohash-plugin
* Blog: http://blog.improve-osm.org/en/2018/03/geohash-josm-plug-in/
* JOSM Wiki: https://wiki.openstreetmap.org/wiki/JOSM/Plugins/GeoHash