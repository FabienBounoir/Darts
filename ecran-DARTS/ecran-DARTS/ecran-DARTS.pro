#-------------------------------------------------
#
# Project created by Bounoir Fabien 2020-02-12T11:55:51
#
#-------------------------------------------------

QT       += core gui \
            bluetooth \
            multimedia \
            multimediawidgets

greaterThan(QT_MAJOR_VERSION, 4): QT += widgets

TARGET = ecran-DARTS
TEMPLATE = app

# The following define makes your compiler emit warnings if you use
# any feature of Qt which has been marked as deprecated (the exact warnings
# depend on your compiler). Please consult the documentation of the
# deprecated API in order to know how to port your code away from it.
DEFINES += QT_DEPRECATED_WARNINGS

# You can also make your code fail to compile if you use deprecated APIs.
# In order to do so, uncomment the following line.
# You can also select to disable deprecated APIs only up to a certain version of Qt.
#DEFINES += QT_DISABLE_DEPRECATED_BEFORE=0x060000    # disables all the APIs deprecated before Qt 6.0.0

CONFIG += c++11 \
          file_copies

SOURCES += \
        main.cpp \
        ihm.cpp \
        communication.cpp \
        darts.cpp \
        joueur.cpp \
        solution.cpp

HEADERS += \
        ihm.h \
        communication.h \
        darts.h \
        joueur.h \
        solution.h

FORMS += \
        ihm.ui

RESOURCES += \
        ressources.qrc

COPIES += images

images.files = impact

images.path = $$OUT_PWD/

images.base = $$PWD/

CONFIG(release, debug|release):DEFINES+=QT_NO_DEBUG_OUTPUT
