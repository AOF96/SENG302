<template>
  <div id="map">
    <v-snackbar outlined color="error" :timeout="timeout" v-model="snackbar" bottom>{{errorMessage}}</v-snackbar>
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
      this.getMapBounds();
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
        window.google.maps.event.addListenerOnce(map, 'idle', function(){
          this.mapBounds = this.getBounds();
          // var NECorner = this.mapBounds.getNorthEast();
          // var SWCorner = this.mapBounds.getSouthWest();
          // // console.log(this.mapBounds);
          // console.log(NECorner.get);
          // console.log(SWCorner);
          console.log(this.mapBounds);
          // alert(this.mapBounds);
        });

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
      getMapBounds(){
        // console.log(this.mapBounds);
      }
    }
  }
</script>

<style scoped>
  @import "../../../public/styles/pages/mapStyle.css";
</style>