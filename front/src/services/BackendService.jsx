import axios from 'axios'
import Utils from '../utils/Utils'
import {alertActions, store} from '../utils/Rdx';

const API_URL = 'http://localhost:8081/api/v1'
const AUTH_URL = 'http://localhost:8081/auth'

function showError(msg) {
    store.dispatch(alertActions.error(msg))
}

axios.interceptors.request.use(
    config => {
        store.dispatch(alertActions.clear())
        let token = Utils.getToken();
        if (token)
            config.headers.Authorization = token;
        return config;
    },
    error => {
        showError(error.message)
        return Promise.reject(error);
    }
)

axios.interceptors.response.use(undefined,
    error =>  {
    if (error.response && error.response.status && [401, 403].indexOf(error.response.status) !== -1)
        showError("Ошибка авторизации");
    else if (error.response && error.response.data && error.response.data.message)
        showError(error.response.data.message);
    else
        showError(error.message);
    return Promise.reject(error);
    })

class BackendService {
  login(login, password) {
    return axios.post(`${AUTH_URL}/login`, {login, password})
  }

  logout() {
    return axios.get(`${AUTH_URL}/logout`, { headers : {Authorization : Utils.getToken()}})
  }

  /* Countries */

retrieveAllCountries(page, limit) {
    return axios.get(`${API_URL}/countries?page=${page}&limit=${limit}`);
}

  retrieveCountry(id) {
      return axios.get(`${API_URL}/countries/${id}`);
  }

  createCountry(country) {
      return axios.post(`${API_URL}/countries`, country);
  }

  updateCountry(country) {
      return axios.put(`${API_URL}/countries/${country.id}`, country);
  }

  deleteCountries(countries) {
      return axios.post(`${API_URL}/deletecountries`, countries);
  }


  /* MUSEUM */

    retrieveAllMuseums(page, limit) {
        return axios.get(`${API_URL}/museums?page=${page}&limit=${limit}`);
    }

    createMuseum(musem) {
      return axios.post(`${API_URL}/museums`,musem);
    }

  deleteMuseums(museums) {
      return axios.post(`${API_URL}/deletemuseums`, museums);
  }


  retrieveMuseum(id) {
      return axios.get(`${API_URL}/museums/${id}`);
  }


  updateMuseum(museum) {
      return axios.put(`${API_URL}/museums/${museum.id}`, museum);
  }

  /* User */

    retrieveAllUsers(page, limit) {
        return axios.get(`${API_URL}/users?page=${page}&limit=${limit}`);
    }

    createUser(user) {
      return axios.post(`${API_URL}/user`,user);
    }

  deleteUsers(users) {
      return axios.post(`${API_URL}/deleteusers`, users);
  }


  retrieveUser(id) {
      return axios.get(`${API_URL}/users/${id}`);
  }


  updateUser(user) {
      return axios.put(`${API_URL}/users/${user.id}`, user);
  }


  /* ARTIST */

    retrieveAllArtists(page, limit) {
        return axios.get(`${API_URL}/artists?page=${page}&limit=${limit}`);
    }

    createArtist(artist) {
      return axios.post(`${API_URL}/artists`,artist);
    }

  deleteArtists(artist) {
      return axios.post(`${API_URL}/deleteartists`, artist);
  }


  retrieveArtist(id) {
      return axios.get(`${API_URL}/artists/${id}`);
  }


  updateArtist(artist) {
      return axios.put(`${API_URL}/artists/${artist.id}`, artist);
  }


  /* PAINTING */

    retrieveAllPaintings(page, limit) {
        return axios.get(`${API_URL}/paintings?page=${page}&limit=${limit}`);
    }

    createPainting(painting) {
      return axios.post(`${API_URL}/paintings`,painting);
    }

  deletePaintings(painting) {
      return axios.post(`${API_URL}/deletepaintings`, painting);
  }


  retrievePainting(id) {
      return axios.get(`${API_URL}/paintings/${id}`);
  }


  updatePainting(painting) {
      return axios.put(`${API_URL}/paintings/${painting.id}`, painting);
  }


}

export default new BackendService()