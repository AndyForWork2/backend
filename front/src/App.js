import {connect} from "react-redux";

import './App.css';
import React from "react";
import {BrowserRouter, Route, Routes, Navigate} from "react-router-dom";
import NavigationBar from "./components/NavigationBar";
import Home from "./components/Home";
import Login from "./components/Login";
import Utils from "./utils/Utils";
import {useState} from "react";
import SideBar from "./components/SideBar";
import CountryListComponent from './components/fromServer/CountryListComponent'
import CountryComponent from './components/fromServer/CountryComponent'
import MuseumListComponent from './components/fromServer/MuseumListComponent'
import MuseumComponent from './components/fromServer/MuseumComponent'
import UserListComponent from './components/fromServer/UserListComponent'
import UserComponent from './components/fromServer/UserComponent'
import ArtistListComponent from './components/fromServer/ArtistListComponent'
import ArtistComponent from './components/fromServer/ArtistComponent'
import PaintingListComponent from './components/fromServer/PaintingListComponent'
import PaintingComponent from './components/fromServer/PaintingComponent'
import MyAccountComponent from './components/fromServer/MyAccountComponent'

const ProtectedRoute = ({children}) => {
    let user = Utils.getUser();
    return user ? children : <Navigate to={'/login'} />
};


const App = props => {

const [exp,setExpanded] = useState(true);
    return (
        <div className="App">
            <BrowserRouter>
                <NavigationBar toggleSideBar={() =>
                    setExpanded(!exp)}/>
                    <div className="wrapper">
                        <SideBar expanded={exp} />
                        <div className="container-fluid">
                            { props.error_message &&  <div className="alert alert-danger m-1">{props.error_message}</div>}
                            <Routes>
                                <Route path="login" element={<Login />}/>
                                <Route path="home" element={<ProtectedRoute><Home/></ProtectedRoute>}/>
                                <Route path="countries" element={<ProtectedRoute> <CountryListComponent/> </ProtectedRoute>}/>
                                <Route path="countries/:id" element={<ProtectedRoute><CountryComponent/></ProtectedRoute>}/>
                                <Route path="museums" element={<ProtectedRoute> <MuseumListComponent/> </ProtectedRoute>}/>
                                <Route path="museums/:id" element={<ProtectedRoute><MuseumComponent/></ProtectedRoute>}/>
                                <Route path="users" element={<ProtectedRoute> <UserListComponent/> </ProtectedRoute>}/>
                                <Route path="users/:id" element={<ProtectedRoute><UserComponent/></ProtectedRoute>}/>
                                <Route path="artists" element={<ProtectedRoute> <ArtistListComponent/> </ProtectedRoute>}/>
                                <Route path="artists/:id" element={<ProtectedRoute><ArtistComponent/></ProtectedRoute>}/>
                                <Route path="paintings" element={<ProtectedRoute> <PaintingListComponent/> </ProtectedRoute>}/>
                                <Route path="paintings/:id" element={<ProtectedRoute><PaintingComponent/></ProtectedRoute>}/>
                                <Route path="my_account" element={<ProtectedRoute><MyAccountComponent/></ProtectedRoute>}/>

                            </Routes>
                        </div>
                    </div>
            </BrowserRouter>
        </div>
    );
}

function mapStateToProps(state) {
    const { msg } = state.alert;
    return { error_message: msg };
}

export default connect(mapStateToProps)(App);