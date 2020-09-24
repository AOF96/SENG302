<template>
  <div class="mapPageContainer">
    <div id="map">
      <v-snackbar outlined color="error" :timeout="timeout" v-model="snackbar" bottom>{{errorMessage}}</v-snackbar>
    </div>
    <div id="legend">
    </div>
  </div>
</template>

<script>
  import {mapGetters, mapState, mapActions} from "vuex";
  import {apiActivity} from "@/api";
  import mapStyles from "../../util/mapStyles"

  const BASE_URL = process.env.VUE_APP_BASE_URL;

  export default {
    name: "Map",
    data: function () {
      return {
        gmap: null,
        geocoder: null,
        errorMessage: null,
        snackbar: false,
        timeout: 2000,
        searchLatitude: null,
        searchLongitude: null,
        searchedType: null,
        mapStyle: "light",
        mapBounds: null,
        mapActivities: [],
        activities: [],
      }
    },
    props: ['darkModeGlobal'],
    computed: {
      ...mapState(["user"]),
      ...mapGetters(["user"]),
    },
    mounted() {
      this.loadMap();
    },

    watch: {
      mapBounds: function () {
        this.mapActivities = [];
        this.getActivitiesInRange();
      }
    },

    methods: {
      ...mapActions(["getDataFromUrl"]),

      /**
       * Loads the map onto the page and centres on the users home city.
       * Adds a marker on the city's centre. Uses bounds of the viewport to retrieve activities in that range.
       */
      loadMap() {
        this.geocoder = new window.google.maps.Geocoder();
        let userPosition = new window.google.maps.LatLng(this.user.location.latitude, this.user.location.longitude);
        this.gmap = new window.google.maps.Map(document.getElementById("map"), {
          center: userPosition,
          zoom: 12,
          styles: mapStyles[this.darkModeGlobal ? "dark" : "light"],
          zoomControl: false,
          mapTypeControl: true,
          scaleControl: false,
          streetViewControl: false,
          rotateControl: false,
          fullscreenControl: false
        });

        let self = this;
        window.google.maps.event.addListener(this.gmap, 'idle', function () {
          let mapBounds = this.getBounds();
          let NECorner = mapBounds.getNorthEast();
          let SWCorner = mapBounds.getSouthWest();
          let coordinates = {
            NELat: NECorner.lat(),
            NELong: NECorner.lng(),
            SWLat: SWCorner.lat(),
            SWLong: SWCorner.lng()
          };
          apiActivity.getActivityInRange(coordinates.SWLat, coordinates.NELat, coordinates.SWLong, coordinates.NELong)
              .then(response => {
                self.activities = response.data;
                self.createActivityMarkers(self.gmap);
              })
        });

        this.setThemeCheckEvent(this.gmap);
        this.createLegend(this.gmap);
        this.createHomeMarker(this.gmap, userPosition);
        this.createSearch(this.gmap);
      },

      /**
       * Creates a blue marker at the users home location
       * @param map
       * @param position
       */
      createHomeMarker(map, position) {
        let homeIcon = {
          url: "https://i.imgur.com/mNfVgmC.png",
          scaledSize: new window.google.maps.Size(20, 20),
          origin: new window.google.maps.Point(0, 0),
          anchor: new window.google.maps.Point(10, 10)
        };

        let marker = new window.google.maps.Marker({
          map: map,
          position: position,
          icon: homeIcon
        });

        let contentString = '<div id="content">' +
            '<h2>Your Location</h2>' +
            '<div id="bodyContent">' +
            this.locationToString(this.user.location) +
            '</div>' +
            '</div>';

        let infowindow = new window.google.maps.InfoWindow({
          content: contentString
        });

        marker.addListener('click', function () {
          infowindow.open(map, marker);
        });

        window.google.maps.event.addListener(map, "click", function () {
          infowindow.close();
        });
      },

      /**
       * Formats Date object to pretty English
       * @param date
       */
      dateFormatterToEnglish: function (date) {
        const options = {
          weekday: "long",
          day: "2-digit",
          month: "long",
          year: "numeric",
          hour12: true,
          hour: "2-digit",
          minute: "2-digit"
        };
        return date.toLocaleTimeString("en-US", options);
      },

      /**
       * Gets activities within the bounds of the map
       */
      getActivitiesInRange() {
        let NECorner = this.mapBounds.getNorthEast();
        let SWCorner = this.mapBounds.getSouthWest();
        let coordinates = {
          NELat: NECorner.lat(),
          NELong: NECorner.lng(),
          SWLat: SWCorner.lat(),
          SWLong: SWCorner.lng()
        };
        apiActivity.getActivityInRange(coordinates.SWLat, coordinates.NELat, coordinates.SWLong, coordinates.NELong)
            .then(response => {
              this.activities = response.data;
              this.createActivityMarkers(this.gmap);
            })
      },

      /**
       * Creates markers for the activities on the map
       * @param map
       */
      createActivityMarkers(map) {
        let innerThis = this;
        let activityMarkerIcon = {
          url: "https://i.imgur.com/MUWKzz9.png",
          scaledSize: new window.google.maps.Size(30, 30),
          origin: new window.google.maps.Point(0, 0),
          anchor: new window.google.maps.Point(15, 30)
        };

        for (let activity of this.activities) {
          activityMarkerIcon.url = this.pickMarkerImage(activity);

          let activityPosition = new window.google.maps.LatLng(activity.location.latitude, activity.location.longitude);
          let pos = innerThis.mapActivities.length;
          innerThis.mapActivities.push(new window.google.maps.Marker({
            map: map,
            position: activityPosition,
            icon: activityMarkerIcon,
          }));

          let contentString;

          if (activity.continuous === true) {
            contentString = '<div class="content">' +
                '<h1 class="activityPopupActivityVisibility" style="background:' + this.getVisibilityColour(activity.visibility) + ';">' + activity.visibility + '</h1>' +
                '<h1 class="activityPopupLocation">' + this.locationToString(activity.location) + '</h1>' +
                '<h1 class="activityPopupTitle">' + activity.name + '</h1>' +
                '<h1 class="activityPopupDescription">' + activity.description + '</h1>' +
                '<h1 class="activityPopupActivityTypes">' + activity.activity_types + '</h1>' +
                '<h1 class="activityPopupActivityFollowers">' + activity.numFollowers + ' followers</h1>' +
                '<hr class="activityPopupActivityLine">' +
                '<a href= "' + BASE_URL + 'activity/' + activity.id + '"><button class="activityPopupActivityButton">Go to Activity</button></a>' +
                '</div>';
          } else {

            let activityStartDate = this.dateFormatterToEnglish(new Date(activity.start_time));
            let activityEndDate = this.dateFormatterToEnglish(new Date(activity.end_time));

            contentString = '<div class="content">' +
                '<h1 class="activityPopupActivityVisibility" style="background:' + this.getVisibilityColour(activity.visibility) + ';">' + activity.visibility + '</h1>' +
                '<h1 class="activityPopupLocation">' + this.locationToString(activity.location) + '</h1>' +
                '<h1 class="activityPopupTitle">' + activity.name + '</h1>' +
                '<h1 class="activityPopupDescription">' + activity.description + '</h1>' +
                '<h1 class="activityPopupStartTime">' + "Starts: " + activityStartDate + '</h1>' +
                '<h1 class="activityPopupEndTime">' + "Ends: " + activityEndDate + '</h1>' +
                '<h1 class="activityPopupActivityTypes">' + activity.activity_types + '</h1>' +
                '<h1 class="activityPopupActivityFollowers">' + activity.numFollowers + ' followers</h1>' +
                '<hr class="activityPopupActivityLine">' +
                '<a href= "' + BASE_URL + 'activity/' + activity.id + '"><button class="activityPopupActivityButton">Go to Activity</button></a>' +
                '</div>';
          }

          let infowindow = new window.google.maps.InfoWindow({
            content: contentString
          });

          innerThis.mapActivities[pos].addListener('click', function () {
            infowindow.open(map, innerThis.mapActivities[pos]);
          });

          window.google.maps.event.addListener(map, "click", function () {
            infowindow.close();
          });
        }
      },

      /**
       * Decides on which marker image to use for an activity
       */
      pickMarkerImage(activity) {
        let url;
        if (activity.visibility === "public") {
          if (activity.authorId === this.user.profile_id) {
            url = "https://i.imgur.com/Hz5QgGa.png"
          } else {
            url = "https://i.imgur.com/MUWKzz9.png"
          }
        } else if (activity.visibility === "restricted") {
          if (activity.authorId === this.user.profile_id) {
            url = "https://i.imgur.com/61rB4dm.png"
          } else {
            url = "https://i.imgur.com/Y0JUUox.png"
          }
        } else if (activity.visibility === "private") {
          if (activity.authorId === this.user.profile_id) {
            url = "https://i.imgur.com/jNY9HSw.png"
          } else {
            url = "https://i.imgur.com/lanhJgs.png"
          }
        }
        return url;
      },

      /**
       * Returns the correct colour for a given activity visibility
       * @param visibility
       * @returns {string}
       */
      getVisibilityColour(visibility) {
        let colour = "#1dca92";
        switch (visibility) {
          case "private":
            colour = "#ff4f4a";
            break;
          case "restricted":
            colour = "#ff843c";
            break;
          case "public":
            colour = "#1dca92";
            break;
        }
        return colour;
      },

      /**
       * Converts given location object to a formatted string
       * @param location
       * @returns {string}
       */
      locationToString(location) {
        let city = location.city;
        let state = location.state;
        let country = location.country;
        let streetAddress = location.street_address;
        let outputString = "";

        if (location === {}) {
          outputString = "No Location Set"
        } else {
          outputString += streetAddress;
          if (city !== "") {
            if (outputString !== "") {
              outputString += ", "
            }
            outputString += city;
          }
          if (state !== "") {
            if (outputString !== "") {
              outputString += ", "
            }
            outputString += state;
          }
          if (country !== "") {
            if (outputString !== "") {
              outputString += ", "
            }
            outputString += country;
          }
          if (outputString === "") {
            outputString = "No Location Set"
          }
        }
        return outputString;
      },

      /**
       * Navigates to the users profile
       */
      goToProfile() {
        this.$router.push('/profile/' + this.user.profile_id)
      },

      /**
       * Creates an event handler to check if the theme has changed
       * @param map
       */
      setThemeCheckEvent(map) {
        let outer = this;
        window.google.maps.event.addDomListener(window, 'click', function() {
          map.setOptions({
            styles: mapStyles[outer.darkModeGlobal ? "dark" : "light"]
          });
        });
      },

      /**
       * Create a legend for the map to display what the different pins mean
       * @param map
       */
      createLegend(map) {
        let iconBase = 'https://i.imgur.com/';
        let icons = {
          yourLocation: {
            name: 'Your Location',
            icon: iconBase + 'mNfVgmC.png'
          },
          otherLocation: {
            name: "Other's Location",
            icon: iconBase + 'Sq7ZPA2.png'
          },
          publicActivity: {
            name: 'Public',
            icon: iconBase + 'MUWKzz9.png'
          },
          restrictedActivity: {
            name: 'Restricted',
            icon: iconBase + 'Y0JUUox.png'
          },
          privateActivity: {
            name: 'Private',
            icon: iconBase + 'lanhJgs.png'
          },
          publicActivityOwned: {
            name: 'Public - Created',
            icon: iconBase + 'Hz5QgGa.png'
          },
          restrictedActivityOwned: {
            name: 'Restricted - Created',
            icon: iconBase + '61rB4dm.png'
          },
          privateActivityOwned: {
            name: 'Private - Created',
            icon: iconBase + 'jNY9HSw.png'
          }
        };
        let legend = document.getElementById('legend');
        for (let key in icons) {
          let type = icons[key];
          let name = type.name;
          let icon = type.icon;
          let div = document.createElement('div');
          div.innerHTML = '<img src="' + icon + '"> ' + '<h3>' + name + '</h3>';
          legend.appendChild(div);
        }
        map.controls[window.google.maps.ControlPosition.RIGHT_BOTTOM].push(document.getElementById('legend'));
      },

      /**
       * Checks if search exists and creates marker if it does
       */
      createSearch(map) {
        if (this.$route.params.coordinates !== undefined) {
          this.parseCoordinates();
          this.createSearchMarker(map);
        }
      },

      /**
       * Parses the Coordinates in the URL (@{latitude},{longitude}), extracting the latitude and longitude.
       */
      parseCoordinates() {
        let searchVars = this.$route.params.coordinates.split('@');
        this.searchedType = searchVars[0];
        let coordinates = searchVars[1].split(',');
        this.searchLatitude = coordinates[0];
        this.searchLongitude = coordinates[1];
      },

      /**
       * Creates a search marker and centers the map on the search. Changes pin depending on what the searched location is
       * @param map
       */
      createSearchMarker(map) {
        let icon;
        let searchPosition = new window.google.maps.LatLng(this.searchLatitude, this.searchLongitude);

        if (this.searchedType === "user") {
          icon = {
            url: "https://i.imgur.com/Sq7ZPA2.png", // Change this icon
            scaledSize: new window.google.maps.Size(20, 20),
            origin: new window.google.maps.Point(0, 0),
            anchor: new window.google.maps.Point(13, 26)
          };
          new window.google.maps.Marker({
            map: map,
            position: searchPosition,
            icon: icon
          });
        }

        map.setCenter(searchPosition);
      }
    }
  }
</script>

<style scoped>
  @import "../../../public/styles/pages/mapStyle.css";
</style>