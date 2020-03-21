<template>
<div id="settingsWrap">
    <UserSettingsMenu />
    <div class="settingsContent">
        <h1>Edit Passport Countries</h1>
        <hr>
        <div class="countryBox" v-for="country in user.user.tmp_passports" v-bind:key="country">
            <h4 class="countryDisplay">{{country}}</h4>
            <button class="removeCountryButton" v-on:click="removePassportCountries(country)">remove</button>
            <div class="floatClear"></div>
        </div>

        <div id="countryActions">
          <form @submit.prevent>
            <select
                v-model="adding_country"
                name="passportCountries"
                placeholder="Passport Countries"
                value="Passport Countries"
                id="passportCountriesInput"
                required
            >
                <option selected disabled hidden>Passport Countries</option>
                <option v-for="addingCountry in countries_option" v-bind:key="addingCountry">
                    {{addingCountry}}
                </option>
            </select>
            <button id = "addPassportButton" v-on:click="addPassportCountries()">Add</button>
              <button id ="saveChangesButton" v-on:click="savePassportCountries()">Save</button>
          </form>
        </div>
    </div>
</div>
</template>

<script>
import UserSettingsMenu from '@/components/Settings/UserSettingsMenu'


import axios from "axios";
const COUNTRIES_URL = 'https://restcountries.eu/rest/v2/all?fields=name'
import {apiUser} from "../../api";
import { mapState, mapActions } from 'vuex'

export default {
    components: {
        UserSettingsMenu
    },
    data() {
        return {
            countries_option: [],
            adding_country: "Passport Countries",
            num_of_countries: 1,
        }
    },
    computed: {
        ...mapState(['user']),
    },
    created: function() {
        axios.get(COUNTRIES_URL)
            .then((response) => {
                const countries = []
                const data = response.data
                for (let country in data) {
                    let country_name = data[country].name
                    countries.push(country_name)
                }

                for(let country of this.user.user.passports) {
                    const index = countries.indexOf(country)
                    if (index === -1) continue
                    countries.splice(index, 1)
                }
                this.countries_option = countries
            })
            .catch(error => console.log(error))
    },
    mounted() {
        this.startUp()
    },
    methods: {
        ...mapActions(['updatePassports', 'updateTmpPassports']),
         startUp() {
            console.log('init');
            this.user.user.tmp_passports = this.user.user.passports.slice()
        },
        removePassportCountries(country) {
            const index = this.user.user.tmp_passports.indexOf(country);
            if (index === -1) return;
            this.user.user.tmp_passports.splice(index, 1);
            this.countries_option.push(country);
            this.countries_option.sort();
            this.updateTmpPassports(this.user.user)
        },
        addPassportCountries() {
            this.user.user.tmp_passports.push(this.adding_country);
            const index = this.countries_option.indexOf(this.adding_country);
            if (index == -1) return;
            this.countries_option.splice(index, 1);
            this.adding_country = "Passport Countries";
            this.updateTmpPassports(this.user.user)
        },
        savePassportCountries() {
            this.updatePassports(this.user.user);
            console.log(this.user.user.passports);
            apiUser.editProfile(this.user.user.profile_id, this.user.user.firstname, this.user.user.lastname, this.user.user.middlename,
                this.user.user.nickname, this.user.user.primary_email, this.user.user.bio, this.user.user.date_of_birth, this.user.user.gender,
                this.user.user.fitness, this.user.user.additional_email, this.user.user.passports, this.user.user.activities);
        }
    }
}
</script>
