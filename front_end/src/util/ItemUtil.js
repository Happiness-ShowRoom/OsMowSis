import React from 'react';

import { Tooltip, OverlayTrigger } from 'react-bootstrap';

import grass from '../img/grass.svg';
import cutGrass from '../img/cut_grass.svg';
import gopher from '../img/gopher.svg';
import mowerEast from '../img/mower_east.svg';
import mowerSouthEast from '../img/mower_southeast.svg';
import mowerNorthEast from '../img/mower_northeast.svg';
import mowerNorth from '../img/mower_north.svg';
import mowerSouth from '../img/mower_south.svg';
import mowerWest from '../img/mower_west.svg';
import mowerNorthWest from '../img/mower_northwest.svg';
import mowerSouthWest from '../img/mower_southwest.svg';
import chargingPad from '../img/charging_pad.svg';
import disabledMower from "../img/disabled_mower.svg"
import fence from '../img/fence.svg';
import brick from '../img/brickwall.svg';

function getMower(item) {
    const att = item.split("_")
  
    return ["mower", att[1], att[2]]
  }
  
function getItem(item) {
    const img = {
      "chargingpad": chargingPad,
      "gopher": gopher,
      "empty": cutGrass,
      "grass": grass,
      "fence": fence,
      "brick": brick,
      "mower_east": mowerEast,
      "mower_southeast": mowerSouthEast,
      "mower_northeast": mowerNorthEast,
      "mower_north": mowerNorth,
      "mower_south": mowerSouth,
      "mower_west": mowerWest,
      "mower_northwest": mowerNorthWest,
      "mower_southwest": mowerSouthWest,
      "disabled_mower": disabledMower
    }
  
    return img[item] ? img[item] : brick
}

function renderTooltip(energy) {
    return <Tooltip>{"Energy: " + energy}</Tooltip>;
  }  

export default function cleanItems(items) {
    items = items ? items : []
    const height = "24"
    const width = "24"
    let images = []
    if (items.length === 17) {
        for (let i = 0; i < items.length; i++) {
            let item = items[i] + ""
            var foo = ""
            if (item.includes("disabled")) {
                foo = (
                    <div>
                        <OverlayTrigger
                            placement="right"
                            delay={{ show: 250, hide: 400 }}
                            overlay={renderTooltip("0")}
                        >
                            <img 
                                src={getItem("disabled_mower")}
                                alt={"disabled_mower"}
                                height={height}
                                width={width} 
                            />
                        </OverlayTrigger>
                        {/* <p>{"Energy: 0"}</p> */}
                    </div>
                )    
            } else if (item.includes("mower")) {
                let mower = getMower(item)
                foo = (
                    <div>
                        <OverlayTrigger
                            placement="right"
                            delay={{ show: 250, hide: 400 }}
                            overlay={renderTooltip(mower[2])}
                        >
                            <img 
                                src={getItem(mower[0]+"_"+mower[1].toLowerCase())}
                                alt={(mower[0]+"_"+mower[1]).toLowerCase()}
                                height={height}
                                width={width} 
                            />
                        </OverlayTrigger>
                        {/* <p>{"Energy: " + mower[2]}</p> */}
                    </div>
                )
            } else if (item.includes("gopher")) {
                foo = (
                    <img src={getItem("gopher")} alt={"gopher"} height={height} width={width} />
                )
            } else {
                foo = (
                    <img src={getItem(items[i])} alt={items[i]} height={height} width={width} />
                )
            }
        
            images.push(foo)
        }
    } else {
        for (let i = 0; i < 17; i++) {
        
        images.push(
            <img src={getItem("brick")} alt={"brick"} height={height} width={width} />
        )
        }
    } 

    return images
}
