<template>
  <div>
    <div id="locationSettingsMap"></div>
    <hr/>
    <v-container>
      <v-row justify="center" align="center"><h4>Select on map or type in the box</h4></v-row>
    </v-container>
    <div class="locationFieldDiv">
      <v-text-field id="activityLocationAutocomplete" class="locationInput" label="Address" outlined dense></v-text-field>
    </div>
  </div>
</template>

<script>
  export default {
    name: "ActivityLocationSettings",

    data() {
      return {
        marker: null,
      }
    },

    mounted() {
      this.loadMap();
    },

    methods: {
      /**
       * Loads the map onto the page and centres on the users home city.
       * Adds a marker on the city's centre.
       */
      loadMap() {
        if (!window.google) {
          return;
        }
        this.geocoder = new window.google.maps.Geocoder();

        let map = new window.google.maps.Map(document.getElementById("locationSettingsMap"), {
          center: {
            lat: -34.397,
            lng: 150.644
          },
          zoom: 9,
          streetViewControl: false,
          fullscreenControl: false,
          rotateControl: false,
          mapTypeControl: false
        });

        map.addListener('click', function (e) {
          if (this.marker != null) {
            this.marker.setMap(null);
          }
          this.marker = new window.google.maps.Marker({
            position: e.latLng,
            map: map
          });
          map.panTo(e.latLng);
        });

        this.setMapCentre(map);
        this.loadLocationAutocomplete();
      },
      /**
       * Centres the map on the users current location
       */
      setMapCentre(map) {
        this.geocoder.geocode({'address': "Christchurch"}, function (results, status) {
          if (status === 'OK') {
            map.setCenter(results[0].geometry.location);
            this.marker = new window.google.maps.Marker({
              map: map,
              position: results[0].geometry.location
            });
          } else {
            this.snackbarText = status;
            this.snackbarColour = "error";
            this.snackbar = true;
          }
        });
      },
      /** load google autocomplete on create activity page **/
      loadLocationAutocomplete() {
        if (!window.google) {
          return;
        }
        this.autocomplete = new window.google.maps.places.Autocomplete(
            document.getElementById("activityLocationAutocomplete"),
            {
              types: ["address"],
            }
        );
      }
    }
  }
</script>

<style scoped>

</style>