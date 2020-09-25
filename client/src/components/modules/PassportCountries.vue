<template>
  <v-card class="profileModule"
          :loading="loadingCountries"
          style="border-radius: 14px"
          v-if="country_dicts.length !== 0"
  >
    <h1>Passport Countries</h1>
    <div class="passportCountriesContainer">
      <div class="passportCountryContainer" v-for="(country_dict, i) in country_dicts" v-bind:key="i">
        <hr>
        <h2>{{ country_dict[0] }}</h2>
        <img :src="country_dict[1]"/>
        <div class="floatClear"></div>
      </div>
    </div>
  </v-card>
</template>

<script>
import {mapGetters, mapActions} from "vuex";

const COUNTRIES_URL = "https://restcountries.eu/rest/v2/all";

export default {
  name: "PassportCountries",
  props: {
    passports: Array
  },
  data() {
    return {
      country_dicts: [],
      loadingCountries: true,
    };
  },
  computed: {
    ...mapGetters(["user"])
  },
  mounted() {
    this.startUp();
  },
  methods: {
    ...mapActions(["getDataFromUrl"]),

    /**
     * Function uses the country api and checks it against the ist of passport countries selected by the user and then
     * creates a dictionary of name mapped to the country code which is further used to retrieve the flags and display
     * them on the profile page.
     */
    startUp() {
      this.getDataFromUrl(COUNTRIES_URL)
        .then(response => {
          var country_dicts = [];
          const data = response.data;
          for (var each_country of data) {
            for (var country of this.passports) {
              if (country == each_country.name) {
                country_dicts.push([
                  each_country.name,
                  "https://restcountries.eu/data/".concat(
                      each_country.alpha3Code.toLowerCase(),
                      ".svg"
                  )
                ]);
              }
            }
          }
          this.country_dicts = country_dicts;
          this.loadingCountries = false;
        })
        .catch(error => console.log(error));
    }
  }
};
</script>
