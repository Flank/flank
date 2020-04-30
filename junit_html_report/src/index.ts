import { IResultGroup } from 'types/index';
import { createGroupNode } from 'components/group';

import './styles.scss';

class ResultList {
  results: IResultGroup[];
  node: Node;

  constructor(results: IResultGroup[]) {
    this.results = results;

    this.node = document.createElement('ul');

    results.forEach((group) => {
      this.node.appendChild(createGroupNode(group));
    });
  }

  attach(selector: string) {
    const el = document.querySelector(selector);
    if (!el) {
      console.warn(`No such element - "${selector}"`);
      return;
    }

    el.appendChild(this.node)
  }
}

function init() {
  let results: IResultGroup[];

  try {
    results = JSON.parse((window as any)['resultData']);
  } catch (err) {
    console.warn(err.message);
    results = [];
  }


  if (results.length === 0) {
    console.warn('No results found.');
    return;
  }

  const list = new ResultList(results);
  list.attach('#results');
}

init();
