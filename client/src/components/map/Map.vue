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

        let position = new window.google.maps.LatLng(this.user.location.latitude, this.user.location.longitude);

        let map = new window.google.maps.Map(document.getElementById("map"), {
          center: position,
          zoom: 12,
          styles: mapStyles[this.mapStyle],
          zoomControl: false,
          mapTypeControl: true,
          scaleControl: false,
          streetViewControl: false,
          rotateControl: false,
          fullscreenControl: false
        });

        this.createControl(map);

        this.createHomeMarker(map, position);
      },

      /**
       * Creates a blue marker at the users home location
       * @param map
       * @param position
       */
      createHomeMarker(map, position) {
        var homeIcon = {
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
      }
    }
  }
</script>

<style scoped>
  @import "../../../public/styles/pages/mapStyle.css";
</style>