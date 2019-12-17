import React from 'react';
import { Button } from 'react-bootstrap';

export default function Controls(props) {
    return (
        <div className="row">
            <div className="col">
                <Button variant="info" onClick={props.handleNext}>Next Step</Button>
            </div>
            <div className="col">
                <Button variant="warning" onClick={props.handleFastForward}>Fast Forward</Button>
            </div>
            <div className="col">
                <Button variant="danger" onClick={props.handleStop}>Stop</Button>
            </div>
        </div>
    );
}