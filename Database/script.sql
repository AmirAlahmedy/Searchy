create table pages
(
    id    int auto_increment
        primary key,
    url   text       not null,
    title text       not null,
    h1    text       not null,
    h2    text       not null,
    h3    text       not null,
    h4    text       not null,
    h5    text       not null,
    h6    text       not null,
    body  mediumtext not null,
    alt   text       not null,
    meta  mediumtext       not null,
    date  int        null,
    country varchar(255)    null,
    indexed tinyint(1) default 0 null,
    constraint pages_url_uindex
        unique (url) using hash
);

create table Crawler_Backup
(
    id    int auto_increment
        primary key,
    url   text       not null,
    constraint crawler_url_uindex
        unique (url) using hash
);

create table Terms
(
    id      int auto_increment,
    Term    varchar(255)         not null,
    Page_Id int                  not null,
    TF      double               null,
    IDF     double               null,
    Title   tinyint(1) default 0 null,
    Meta    tinyint(1) default 0 null,
    H1      tinyint(1) default 0 null,
    H2      tinyint(1) default 0 null,
    H3      tinyint(1) default 0 null,
    H4      tinyint(1) default 0 null,
    H5      tinyint(1) default 0 null,
    H6      tinyint(1) default 0 null,
    Alt     tinyint(1) default 0 null,
    primary key (Page_Id, Term),
    constraint Terms_pk
        unique (id),
    constraint Terms_pages__fk
        foreign key (Page_Id) references pages (id)
            on update cascade on delete cascade
);


create table Images
(
    id      int auto_increment,
    term    varchar(255),
    page_Id int         not null,
    src     text        not null,
    primary key (id),
    constraint Images_pages__fk
        foreign key (page_Id) references pages (id)
            on update cascade on delete cascade
);

create table Ranks
(
    pageId int              not null
        primary key,
    PR     double default 0 null,
    constraint Ranks_pages__fk
        foreign key (pageId) references pages (id)
            on update cascade
);

create table Trends
(
    id      int auto_increment,
    name    varchar(255),
    country varchar(255),
    frequency int,
    primary key (name,country),
    constraint Trends_pk
        unique (id)
);

create table Suggestions
(
    id    int auto_increment
        primary key,
    value varchar(255) null,
    constraint Suggestions_value_uindex
        unique (value)
);

