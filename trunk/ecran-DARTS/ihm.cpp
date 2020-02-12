#include "ihm.h"
#include "ui_ihm.h"

Ihm::Ihm(QWidget *parent) :
    QWidget(parent),
    ui(new Ui::Ihm)
{
    ui->setupUi(this);
}

Ihm::~Ihm()
{
    delete ui;
}
