#ifndef IHM_H
#define IHM_H

#include <QMainWindow>

namespace Ui {
class Ihm;
}

class Ihm : public QMainWindow
{
    Q_OBJECT

public:
    explicit Ihm(QWidget *parent = nullptr);
    ~Ihm();

private:
    Ui::Ihm *ui;
};

#endif // IHM_H
