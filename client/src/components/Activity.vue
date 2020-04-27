<template>
    <div>
        <NavBar/>
        <div id="profileBanner">
        </div>
        <div id="profileWrap">
            <div id="profilePublicInfo">
                <h2 class="bigTitle">{{ activity.name }}</h2>

                <div id="userQuickInfoWrapp">
                    <div class="profileInfo">
                        <table id="profileTable">
                            <tr>
                                <td class="profileTableTd">StartTime:</td>
                                <td class="profileTableTd">{{activity.start}}</td>
                            </tr>
                            <tr>
                                <td class="profileTableTd">EndTime:</td>
                                <td class="profileTableTd">{{activity.end}}</td>
                            </tr>
                            <tr>
                                <td class="profileTableTd">Location:</td>
                                <td class="profileTableTd">{{activity.location}}</td>
                            </tr>
                            <tr>
                                <td class="profileTableTd">Duration:</td>
                                <td class="profileTableTd">{{activity.duration}}</td>
                            </tr>
                            <tr>
                                <td class="profileTableTd">Description:</td>
                                <td class="profileTableTd">{{activity.description}}</td>
                            </tr>
                            <tr>
                                <td class="profileTableTd">Activity Types:</td>
                                <td class="profileTableTd">
                                <span v-for="a in activity.activityTypes" :key="a.type_id">
                                    <span v-if="activity.activityTypes.indexOf(a) != activity.activityTypes.length - 1">
                                    {{a.name}},
                                    </span>
                                    <span v-else>
                                        {{a.name}}.
                                    </span>
                                </span>
                                </td>
                            </tr>
                        </table>
                    </div>
                </div>
                <div class="floatClear"></div>
            </div>
        </div>
    </div>
</template>

<script>

  import NavBar from '@/components/NavBar';
  import {mapGetters} from "vuex";

  export default {
    name: "Activity",
    data: function () {
      return {
        activity: {
          name: "Kaikoura Walk",
          start: "25th April 2020",
          end: "26th April 2020",
          location: "Kaikoura, New Zealand",
          duration: "5 minutes",
          description: "It's too long, I'm tired, I'm eating fish and chips.",
          activityTypes: [
            {
              "type_id": 6,
              "name": "Hiking"
            },
            {
              "type_id": 7,
              "name": "Eating"
            },
          ]

        }
      }
    },
    components: {
      NavBar
    },
    computed: {
      ...mapGetters(['user'])
    },
  }
</script>

<style scoped>
    .bigTitle {
        text-align: center;
        margin-left: auto;
        margin-right: auto;
        padding-top: 2rem;
        font-size: 20px;

    }

    .profileInfo {
        font-family: Roboto;
    }

    #userQuickInfoWrapp {
        float: left;
        margin-top: 1px;
    }

    .profileTableTd {
        padding: 10px 20px 10px 0;
    }

</style>