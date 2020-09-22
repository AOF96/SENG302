<template>
  <div class="mapPageContainer">
    <div id="map">
      <v-snackbar outlined color="error" :timeout="timeout" v-model="snackbar" bottom>{{errorMessage}}</v-snackbar>
    </div>
  </div>
</template>

<script>
  import {mapGetters, mapState, mapActions} from "vuex";
  import {apiActivity} from "@/api";
  import mapStyles from "../../util/mapStyles"

  export default {
    name: "Map",
    data: function () {
      return {
        map: null,
        geocoder: null,
        errorMessage: null,
        snackbar: false,
        timeout: 2000,
        searchLatitude: null,
        searchLongitude: null,
        searchedType: null,
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
    methods: {
      ...mapActions(["getDataFromUrl"]),

      /**
       * Loads the map onto the page and centres on the users home city.
       * Adds a marker on the city's centre. Uses bounds of the viewport to retrieve activities in that range.
       */
      loadMap() {
        this.geocoder = new window.google.maps.Geocoder();

        let userPosition = new window.google.maps.LatLng(this.user.location.latitude, this.user.location.longitude);

        let map = new window.google.maps.Map(document.getElementById("map"), {
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

        window.google.maps.event.addListener(map, 'idle', function () {
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
                console.log(response.data);
              })
        });

        this.setThemeCheckEvent(map);
        this.createHomeMarker(map, userPosition);
        this.createSearch(map);
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
        if (this.searchedType === "user") {
          icon = {
            url: "https://i.imgur.com/jNY9HSw.png", // Change this icon
            scaledSize: new window.google.maps.Size(26, 26),
            origin: new window.google.maps.Point(0, 0),
            anchor: new window.google.maps.Point(13, 26)
          };
        } else {
          icon = {
            url: "https://i.imgur.com/MUWKzz9.png", // Change this icon
            scaledSize: new window.google.maps.Size(26, 26),
            origin: new window.google.maps.Point(0, 0),
            anchor: new window.google.maps.Point(13, 26)
          };
        }

        let searchPosition = new window.google.maps.LatLng(this.searchLatitude, this.searchLongitude);

        new window.google.maps.Marker({
          map: map,
          position: searchPosition,
          icon: icon
        });

        map.setCenter(searchPosition);
      }
    }
  }
</script>

<style scoped>
  @import "../../../public/styles/pages/mapStyle.css";
</style>