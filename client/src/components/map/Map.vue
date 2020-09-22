<template>
  <div class="mapPageContainer">
    <div id="styleSelectorDiv" class="styleSelectorDiv">
      <v-radio-group id="styleSelector" v-model="mapStyle">
        <v-radio value="light" label="Light"></v-radio>
        <v-radio value="dark" label="Dark"></v-radio>
      </v-radio-group>
    </div>
    <div id="map">
      <v-snackbar outlined color="error" :timeout="timeout" v-model="snackbar" bottom>{{errorMessage}}</v-snackbar>
    </div>
    <div id="legend"><h3>Legend</h3></div>
  </div>
</template>

<script>
  import {mapGetters, mapState, mapActions} from "vuex";
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
        mapStyle: "light"
      }
    },
    computed: {
      ...mapState(["user"]),
      ...mapGetters(["user"])
    },
    mounted() {
      this.loadMap();
    },
    methods: {
      ...mapActions(["getDataFromUrl"]),
      /**
       * Loads the map onto the page and centres on the users home city.
       * Adds a marker on the city's centre.
       */
      loadMap() {
        this.geocoder = new window.google.maps.Geocoder();

        let userPosition = new window.google.maps.LatLng(this.user.location.latitude, this.user.location.longitude);

        let map = new window.google.maps.Map(document.getElementById("map"), {
          center: userPosition,
          zoom: 12,
          styles: mapStyles[this.mapStyle],
          zoomControl: false,
          mapTypeControl: true,
          scaleControl: false,
          streetViewControl: false,
          rotateControl: false,
          fullscreenControl: false
        });

        var iconBase = 'https://i.imgur.com/';
        var icons = {
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
        var legend = document.getElementById('legend');
        for (var key in icons) {
          var type = icons[key];
          var name = type.name;
          var icon = type.icon;
          var div = document.createElement('div');
          div.innerHTML = '<img src="' + icon + '"> ' + name;
          legend.appendChild(div);
        }
        [window.google.maps.ControlPosition.RIGHT_BOTTOM].push(document.getElementById('legend'));

        this.createControl(map);

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

        let contentString = '<div id="content">'+
            '<h2>Your Location</h2>'+
            '<div id="bodyContent">'+
            this.locationToString(this.user.location) +
            '</div>'+
            '</div>';

        let infowindow = new window.google.maps.InfoWindow({
          content: contentString
        });

        marker.addListener('click', function() {
          infowindow.open(map, marker);
        });

        window.google.maps.event.addListener(map, "click", function() {
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
          if(city !== ""){
            if(outputString !== ""){outputString += ", "}
            outputString += city;
          }
          if(state !== ""){
            if(outputString !== ""){outputString += ", "}
            outputString += state;
          }
          if(country !== ""){
            if(outputString !== ""){outputString += ", "}
            outputString += country;
          }
          if(outputString === ""){
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
       * Creates a control for swapping the map theme
       * @param map
       */
      createControl(map) {
        const styleControl = document.getElementById("styleSelectorDiv");
        map.controls[window.google.maps.ControlPosition.TOP_RIGHT].push(styleControl);

        const styleSelector = document.getElementById("styleSelector");
        map.setOptions({
          styles: mapStyles[this.mapStyle]
        });

        styleSelector.addEventListener("click", () => {
          map.setOptions({
            styles: mapStyles[this.mapStyle]
          });
        });
      },

      /**
       * Checks if search exists and creates marker if it does
       */
      createSearch(map) {
        if (this.$route.params.coordinates !== null) {
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