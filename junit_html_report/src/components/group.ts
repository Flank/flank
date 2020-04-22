import {IResult, IResultGroup} from 'types';
import {createElement} from 'helpers';

const chevron = '<svg><use xlink:href="#icon-chevron"></use></svg>';

function handleGroupClick(ev: MouseEvent) {
  ev.preventDefault();

  const target = ev.currentTarget as HTMLDivElement;

  target.parentElement!.classList.toggle('isOpen');
}

const createGroupLabel = (group: IResultGroup) => createElement(
  'a',
  `${chevron}<span>${group.label} (${group.items.length})</span>`,
  { href: '#' },
  { click: handleGroupClick },
);

const createGroupItem = (item: IResult) => createElement(
  'li',
  `<a target="_blank" href="${item.url}">${item.label}</a>`,
);

export function createGroupNode(group: IResultGroup) {
  const groupLabel = createGroupLabel(group);
  const groupItemList = createElement(
    'ul',
    group.items.map(createGroupItem),
  );

  return createElement(
    'li',
    [groupLabel, groupItemList],
    { class: 'group' },
  );
}
