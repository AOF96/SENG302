<template>
  <div>
    <div id="map">
      <v-snackbar outlined color="error" :timeout="timeout" v-model="snackbar" bottom>{{errorMessage}}</v-snackbar>
    </div>
    <div id="legend"><h3>Legend</h3></div>
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

        let address = this.user.location.street_address + ' ' + this.user.location.city + ' ' + this.user.location.country;
        this.geocoder.geocode({ 'address': address}, function(results, status) {
          if (status === 'OK') {
            map.setCenter(results[0].geometry.location);
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
    }
  }
</script>

<style scoped>
  @import "../../../public/styles/pages/mapStyle.css";
</style>