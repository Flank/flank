import * as React from 'react';
import './App.css';
import {
  IGroupDividerProps,
  GroupedList,
  IGroup
} from 'office-ui-fabric-react/lib/components/GroupedList/index';
import {
  Link,
  ILinkStyleProps,
  ILinkStyles
} from 'office-ui-fabric-react/lib/components/Link/index';
import { GroupHeader } from 'office-ui-fabric-react/lib/components/GroupedList/GroupHeader';
import {
  Selection,
  SelectionMode
} from 'office-ui-fabric-react/lib/utilities/selection/index';
// https://github.com/OfficeDev/office-ui-fabric-react/wiki/Using-icons
import { initializeIcons } from 'office-ui-fabric-react/lib/Icons';
initializeIcons();

import { IItem, dataItems, dataGroups } from './data';

/* tslint:disable:no-debugger */

let _items: IItem[];
let _groups: IGroup[];

class App extends React.Component {
  private _selection: Selection;
  constructor(props: {}) {
    super(props);

    _items = dataItems;
    _groups = dataGroups;

    /* tslint:disable:max-line-length */
    // selection is required.
    // toggleRangeSelected errors on group click if selection is null
    this._selection = new Selection();
    this._onRenderCell = this._onRenderCell.bind(this);
    this._onRenderHeader = this._onRenderHeader.bind(this);
    this._linkStyles = this._linkStyles.bind(this);
  }

  public render(): JSX.Element {
    return (
      <div className='App'>
        <GroupedList
          items={_items}
          onRenderCell={this._onRenderCell}
          selectionMode={SelectionMode.none}
          selection={this._selection}
          groups={_groups}
          groupProps={
            {
              onRenderHeader: this._onRenderHeader
            }
          }
        />
      </div>
    );
  }

  // Props is set via .bind in _onRenderHeader
  private _onGroupHeaderClick(props: IGroupDividerProps, group: IGroup): void {
    if (props.onToggleCollapse !== undefined) { props.onToggleCollapse(group); }
  }

  private _onRenderHeader(props: IGroupDividerProps): JSX.Element {
    props.onGroupHeaderClick = this._onGroupHeaderClick.bind(null, props);

    return <GroupHeader { ...props } />;
  }

  private _linkStyles(props: ILinkStyleProps): ILinkStyles {
    const style = { color: 'blue' };

    return { root: style};
  }

  private _onRenderCell(nestingDepth: number, item: IItem, itemIndex: number): JSX.Element {
    return (
      <div data-selection-index={itemIndex}>
        <span className='GroupedList-name'>
          <Link
            href={item.link}
            getStyles={this._linkStyles}
            target='_blank'
          >
            {item.name}
          </Link>
        </span>
      </div>
    );
  }
}

export default App;
