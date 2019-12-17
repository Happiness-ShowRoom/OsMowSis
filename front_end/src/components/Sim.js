import React from 'react';
import GridRow from  './GridRow'


function Sim(props) {
    let lawn = []
    if(props.lawn) {
        for (var k of Object.keys(props.lawn)) {
            lawn.push(props.lawn[k])
        }
    }

    lawn = lawn[lawn.length-1] ? lawn[lawn.length-1] : []
    

    return (
        <div>
            <GridRow items={lawn[0]} />
            <GridRow items={lawn[1]} />
            <GridRow items={lawn[2]} />
            <GridRow items={lawn[3]} />
            <GridRow items={lawn[4]} />
            <GridRow items={lawn[5]} />
            <GridRow items={lawn[6]} />
            <GridRow items={lawn[7]} />
            <GridRow items={lawn[8]} />
            <GridRow items={lawn[9]} />
            <GridRow items={lawn[10]} />
            <GridRow items={lawn[11]} />
        </div>
    );
}

export default Sim;