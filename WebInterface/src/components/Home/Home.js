import React, {Component, useState} from "react";
import Search from "./Search";
import App from "../../App";
import countries from "../../countries";
import Silogo from "../../silogo.png";
import CountryDropdown from "./Dropdown";
import * as THREE from 'three';

let i = 1;
class Home extends Component {

    state  = {
        country: ''
    }

    selectCountry (val) {
        this.setState({ country: val });
    }

    getCountryValue () {
        return this.state.country;
    }



    render() {
        if(i) {
            i = null;
            var scene = new THREE.Scene();
            var camera = new THREE.PerspectiveCamera(60, window.innerWidth / window.innerHeight, 0.1, 1000);

            var renderer = new THREE.WebGLRenderer();
            renderer.setSize(window.outerWidth, window.outerHeight);
            renderer.setClearColor("#fff");
            renderer.shadowMap.enabled = true;
            document.body.appendChild(renderer.domElement);
            document.getElementsByTagName("canvas")[0].style.display = "block";
            // var loader = new THREE.CubeTextureLoader();
            // loader.setPath( 'textures/cube/pisa/' );
            //
            // var textureCube = loader.load( [
            //     'px.png', 'nx.png',
            //     'py.png', 'ny.png',
            //     'pz.png', 'nz.png'
            // ] );
            //
            // var geometry = new THREE.BoxGeometry();
            // var material = new THREE.MeshBasicMaterial({color: 0x00ff00, map: textureCube});
            // var cube = new THREE.Mesh(geometry, material);
            // cube.castShadow = true;
            // cube.receiveShadow = true;
            // scene.add(cube);

            var geometry = new THREE.TorusKnotBufferGeometry( 3, 1, 256, 32 );
            var material = new THREE.MeshStandardMaterial( { color: 0x6083c2 } );

            let mesh = new THREE.Mesh( geometry, material );
            scene.add( mesh );

            //lights
            scene.add( new THREE.AmbientLight( 0x666666 ) );

            var light = new THREE.DirectionalLight( 0xdfebff, 1 );
            light.position.set( 50, 200, 100 );
            light.position.multiplyScalar( 1.3 );

            light.castShadow = true;

            light.shadow.mapSize.width = 1024;
            light.shadow.mapSize.height = 1024;

            var d = 300;

            light.shadow.camera.left = - d;
            light.shadow.camera.right = d;
            light.shadow.camera.top = d;
            light.shadow.camera.bottom = - d;

            light.shadow.camera.far = 1000;

            scene.add( light );

            camera.position.z = 5;

            var animate = function () {
                requestAnimationFrame(animate);

                // cube.rotation.x += 0.01;
                // cube.rotation.y += 0.01;

                mesh.rotation.z += 0.01;

                renderer.render(scene, camera);
            };
            animate();
        }
        return(
            <div id={"logo-div"}>
            <img src={Silogo} id={'search-logo'} alt="Searchy" style={{
                "alignSelf": "center",
                "verticalAlign": "middle",
                "left": "50%",
                "right": "50%",
                "position": "absolute",
                "transform": "translate(-50%)"
            }}/>
            <Search   items={countries}  country={this.getCountryValue()}/>
            <CountryDropdown value={this.getCountryValue()} onChange={(val) => {this.selectCountry(val); console.log(this.getCountryValue()) }} style={{
                "left": "75%",
                "right": "25%",
                "position": "absolute",
                "bottom": "5%"
            }}/>

            </div>
        );
    }
}

export default Home;