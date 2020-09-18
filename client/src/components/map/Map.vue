<template>
  <div id="map">
    <v-snackbar outlined color="error" :timeout="timeout" v-model="snackbar" bottom>{{errorMessage}}</v-snackbar>
    <div id="hiddenText" hidden="true"></div>
  </div>
</template>

<script>
  import {mapGetters, mapState, mapActions} from "vuex";

  export default {
    name: "Map",
    data: function() {
      return {
        map: null,
        geocoder: null,
        errorMessage: null,
        snackbar: false,
        timeout: 2000,
        mapBounds: null
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
        let map = new window.google.maps.Map(document.getElementById("map"), {
          center: {
            lat: -34.397,
            lng: 150.644
          },
          zoom: 12
        });

        setInterval(this.getMapBounds, 2000, map);

        let address = this.user.location.city;
        this.geocoder.geocode({ 'address': address}, function(results, status) {
          if (status === 'OK') {
            this.gmap.setCenter(results[0].geometry.location);
            new window.google.maps.Marker({
              map: map,
              position: results[0].geometry.location
            });
          } else {
            this.errorMessage = status;
            this.snackbar = true;
          }
        });
      },
      getMapBounds(map){
        let mapBoundsObject = map.getBounds()
        let NECorner = mapBoundsObject.getNorthEast();
        let SWCorner = mapBoundsObject.getSouthWest();
        this.mapBounds = {
          NELat: NECorner.lat(),
          NELong: NECorner.lng(),
          SWLat: SWCorner.lat(),
          SWLong: SWCorner.lng()
        };
        //apiActivity.getActivityInRange(this.mapBounds.SWLat, this.mapBounds.NELat, this.mapBounds.SWLong, this.mapBounds.NELong);
      }
    }
  }
</script>

<style scoped>
  @import "../../../public/styles/pages/mapStyle.css";
</style>