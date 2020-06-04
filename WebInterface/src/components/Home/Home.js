import React, {Component} from "react";
import Search from "./Search";
import countries from "../../countries";
import Silogo from "../../silogo.png";
import CountryDropdown from "./Dropdown";
import * as THREE from 'three';

class Home extends Component {

    state = {
        country: 'Egypt',
        webgl: true
    }

    constructor(props) {
        super(props)

        this.start = this.start.bind(this)
        this.stop = this.stop.bind(this)
        this.animate = this.animate.bind(this)
    }

    componentDidMount() {
        const width = this.mount.clientWidth
        const height = this.mount.clientHeight

        let scene = new THREE.Scene();
        let camera = new THREE.PerspectiveCamera(55, window.innerWidth / window.innerHeight, 0.1, 1000);

        let renderer = new THREE.WebGLRenderer();
        renderer.setSize(window.outerWidth, window.outerHeight);
        renderer.setClearColor("#fff");
        renderer.shadowMap.enabled = true;

        let geometry = new THREE.TorusKnotBufferGeometry(3, 1, 256, 32);
        let material = new THREE.MeshStandardMaterial({color: 0x6083c2});

        let mesh = new THREE.Mesh(geometry, material);
        scene.add(mesh);

        //lights
        scene.add(new THREE.AmbientLight(0x666666));

        let light = new THREE.DirectionalLight(0xdfebff, 1);
        light.position.set(50, 200, 100);
        light.position.multiplyScalar(1.3);

        light.castShadow = true;

        light.shadow.mapSize.width = 1024;
        light.shadow.mapSize.height = 1024;

        let d = 300;

        light.shadow.camera.left = -d;
        light.shadow.camera.right = d;
        light.shadow.camera.top = d;
        light.shadow.camera.bottom = -d;

        light.shadow.camera.far = 1000;

        scene.add(light);

        camera.position.z = 5;
        // renderer.setSize(width, height)

        this.scene = scene
        this.camera = camera
        this.renderer = renderer
        this.material = material
        this.mesh = mesh

        this.mount.appendChild(this.renderer.domElement)
        this.start()
    }

    componentWillUnmount() {
        this.stop()
        this.mount.removeChild(this.renderer.domElement)
    }

    start() {
        if (!this.frameId) {
            this.frameId = requestAnimationFrame(this.animate)
        }
    }

    stop() {
        cancelAnimationFrame(this.frameId)
    }

    animate() {
        this.mesh.rotation.z += 0.01;

        this.renderScene()
        this.frameId = window.requestAnimationFrame(this.animate)
    }

    renderScene() {
        this.renderer.render(this.scene, this.camera)
    }


    selectCountry(val) {
        this.setState({country: val});
    }

    getCountryValue() {
        return this.state.country;
    }

    handleWebGl = (e) => {
        if (e.target.checked) {
            if (document.getElementsByTagName("canvas")[0])
                document.getElementsByTagName("canvas")[0].style.display = "block";
        } else {
            if (document.getElementsByTagName("canvas")[0])
                document.getElementsByTagName("canvas")[0].style.display = "none";
        }
    }

    render() {
        return (
            <div id={"logo-div"}>
                <img src={Silogo} id={'search-logo'} alt="Searchy" style={{
                    "alignSelf": "center",
                    "verticalAlign": "middle",
                    "left": "50%",
                    "right": "50%",
                    "position": "absolute",
                    "transform": "translate(-50%)"
                }}/>
                <Search country={this.getCountryValue()}/>
                <CountryDropdown value={this.getCountryValue()} onChange={(val) => {
                    this.selectCountry(val);
                    console.log(this.getCountryValue())
                }} style={{
                    "left": "75%",
                    "right": "25%",
                    "position": "absolute",
                    "bottom": "5%"
                }}/>
                <div style={{
                    position: "absolute",
                    top: "5%",
                    left: "2%"
                }}>
                    {/*<input type={"checkBox"} id={"id1"} defaultChecked={true} onChange={this.handleWebGl} style={{*/}
                    {/*    marginRight: "5px"*/}
                    {/*}}/>*/}
                    {/*<label for={"id1"}>WebGL</label>*/}
                </div>
                <div
                    style={{ width: '400px', height: '400px' }}
                    ref={(mount) => { this.mount = mount }}
                />
            </div>
        );
    }
}

export default Home;