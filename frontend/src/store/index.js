import Vue from "vue"
import Vuex from "vuex"
import { applicantInfo } from "@/store/applicantInfo"
import { token } from "@/store/token"

Vue.use(Vuex)

export default new Vuex.Store({
  state: {},
  mutations: {},
  actions: {
    async fetchTokenAndSetApplicantInfo(
      { commit, dispatch },
      { name, phoneNumber, email, password, birthday, gender },
    ) {
      await Promise.all([
        dispatch("fetchToken", { name, phoneNumber, email, password, birthday, gender }),
        commit("setApplicantInfo", { name, phoneNumber, email, birthday, gender }),
      ])
    },
  },
  modules: {
    applicantInfo: applicantInfo,
    token: token,
  },
})