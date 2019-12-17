import React from 'react';
import cleanItems from '../util/ItemUtil';

function GridRow(props) {
  const items = cleanItems(props.items)

  
  return (
      <div className="row">
      <div className="col">
        <div className="row">
          <div className="col">
            <div className="row">
              <div className="col">
                {items[0]}
              </div>
              <div className="col">
                {items[1]}
              </div>
            </div>
          </div>
          <div className="col">
            <div className="row">
              <div className="col">
                {items[2]}
              </div>
              <div className="col">
                {items[3]}
              </div>
            </div>
          </div>
          <div className="col">
            <div className="row">
                <div className="col">
                  {items[4]}
                </div>
                <div className="col">
                  {items[5]}
                </div>
            </div>
          </div>
        </div>
      </div>
      <div className="col">
        <div className="row">
          <div className="col">
            <div className="row">
              <div className="col">
                {items[6]}
              </div>
              <div className="col">
                {items[7]}
              </div>
            </div>
          </div>
          <div className="col">
            <div className="row">
              <div className="col">
                {items[8]}
              </div>
              <div className="col">
                {items[9]}
              </div>
            </div>
          </div>
          <div className="col">
            <div className="row">
                <div className="col">
                  {items[10]}
                </div>
                <div className="col">
                  {items[11]}
                </div>
            </div>
          </div>
        </div>
      </div>
      <div className="col">
        <div className="row">
          <div className="col">
            <div className="row">
              <div className="col">
                {items[12]}
              </div>
              <div className="col">
                {items[13]}
              </div>
            </div>
          </div>
          <div className="col">
            <div className="row">
              <div className="col">
                {items[14]}
              </div>
              <div className="col">
                {items[15]}
              </div>
            </div>
          </div>
          <div className="col">
            <div className="row">
                <div className="col">
                  {items[16]}
                </div>
                <div className="col">
                  {/* {items[17]} */}
                </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default GridRow;