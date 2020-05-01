np<template>
    <div>
        <NavBar/>
        <div id="profileBanner">
        </div>
        <div id="profileWrap">
            <div id="profilePublicInfo">
                <div id="ActivityWrap">
                    <ActivityPageInfo :activityInfo="activityData"/>
                </div>
                <div class="floatClear"></div>
            </div>
        </div>
    </div>
</template>

<script>

  import NavBar from '@/components/NavBar';
  import ActivityPageInfo from "@/components/modules/ActivityPageInfo";
  import {mapGetters} from "vuex";

  export default {
    name: "Activity",
    data: function () {
      return {
        activityData: {
          name: "NZ AUS Trail Running Walking",
          start: "25th April 2020",
          end: "26th April 2020",
          location: "Kaikoura, New Zealand",
          duration: "5 minutes",
          description: "Trail running is a popular sport which involves running trails through challenging terrain. Always be sure to follow the Outdoor Safety Code, especially if you plan to head out alone, and seriously consider hiring or purchasing a personal locator beacon. Essential guides and helpful informaiton can be found in the links (left).",
          activityTypes: [
            {
              "type_id": 6,
              "name": "Hiking"
            },
            {
              "type_id": 7,
              "name": "Running"
            },
          ]

        }
      }
    },
    components: {
      NavBar,
      ActivityPageInfo
    },
    computed: {
      ...mapGetters(['user'])
    },
  }
</script>

<style scoped>

    .profileInfo {
        font-family: Roboto;
    }

    .activityTitle {
        text-align: center;
        margin-left: 20px;
        margin-right: auto;
        padding-top: 2rem;
        font-size: 22px;
        color: #1cca92;
        background-color: gold;
        grid-area: Title;

    }

    .activityDescription {
        text-align: center;
        margin-left: 20px;
        margin-right: auto;
        padding-top: 2rem;
        font-size: 18px;
        grid-area: Description;
        background-color: pink;
    }

    .activityLocation {
        text-align: left;
        margin-left: 20px;
        margin-right: auto;
        padding-top: 2rem;
        font-size: 18px;
        grid-area: Location;
    }

    .activityType {
        text-align: left;
        margin-left: 20px;
        margin-right: auto;
        padding-top: 2rem;
        font-size: 18px;
        grid-area: ActivityType;
    }

    .activityStart {
        text-align: left;
        margin-left: 20px;
        margin-right: auto;
        padding-top: 2rem;
        font-size: 18px;
        grid-area: Start;

    }

    .activityEnd {
        text-align: left;
        margin-left: 20px;
        margin-right: auto;
        padding-top: 2rem;
        font-size: 18px;
        grid-area: End;

    }


    .profileTableTd {
        padding: 10px 20px 10px 0;
    }

</style>
