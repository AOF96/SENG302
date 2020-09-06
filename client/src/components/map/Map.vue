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
      loadMap() {
        this.geocoder = new window.google.maps.Geocoder();
        this.map = new window.google.maps.Map(document.getElementById("map"), {
          center: {
            lat: -34.397,
            lng: 150.644
          },
          zoom: 10
        });
        this.setCenterLatLong();
        this.getCenterLatLong();
      },
      setCenterLatLong() {
        let address = this.user.city;
        this.geocoder.geocode({ 'address': address}, function(results, status) {
          if (status === 'OK') {
            this.map.setCenter(results[0].geometry.location);
            new window.google.maps.Marker({
              map: this.map,
              position: results[0].geometry.location
            });
          } else {
            this.errorMessage = status;
            this.snackbar = true;
          }
        });
      },
      getCenterLatLong() {
        console.log(this.user);
        this.getDataFromUrl("https://nominatim.openstreetmap.org/search?city=" + this.user.location.city).then(
            response => {
              console.log(response.data);
            }
        )
      }
    }
  }
</script>

<style scoped>
  @import "../../../public/styles/pages/mapStyle.css";
</style>