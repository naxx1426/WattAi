#include <SFML/Graphics.hpp>
#include <iostream>
#include <string>
#include  <fstream>
#include <vector>
#include <sstream>
int s = 1200;

std::vector<std::vector<int>> readtxt(const std::string& filemname){
    std::vector<std::vector<int>> nums;
    std::ifstream infile(filemname);
    if (!infile.is_open()){
        std::cout<<"Error opening file"<<std::endl;
        return nums;
    }
    std::string line;
    while (std::getline(infile, line)){
        std::istringstream iss(line);
        std::vector<int> row;
        int num;
        while (iss >> num){
            row.push_back(num);
        }
        nums.push_back(row);
    }
    return nums;
}

void draw(sf::RenderWindow& app, sf::RectangleShape rect[], sf::RectangleShape bg, const std::vector<std::vector<int>>& puzzle, int n){
    for (int k = 0; k < puzzle.size(); k++){
        for (int i = 0; i < 9; i++){
            rect[puzzle[k][i]].setSize({int(s / n), int(s / n)});
            rect[puzzle[k][i]].setPosition({int(i%n) * int(s / n), int(i/n) * int(s / n)});
        }

        app.clear();
        app.draw(bg);
        for (int i = 1; i < n * n; i++) {
            app.draw(rect[i]);
        }
        app.display();
        sf::sleep(sf::seconds(0.5));                         
    }
}

int main()
{
    sf::RenderWindow app(sf::VideoMode({1200, 1200}), "n-puzzle");
    app.setFramerateLimit(60);

    std::vector<std::vector<int>> puzzles = readtxt("puzzle.txt");


    sf::RectangleShape bg;
    sf::Texture t0;
    t0.loadFromFile("images/0.png");
    bg.setTexture(&t0);
    bg.setSize({1200,1200});

    sf::RectangleShape rect[16];
    sf::Texture t[16];
    for (int i = 1; i< 16; i++){
        std::string path;
        path = "images/" + std::to_string(i) + ".png";
        t[i].loadFromFile(path);
        rect[i].setTexture(&t[i]);
    }
    while (app.isOpen()){
        draw(app, rect, bg, puzzles, 3);
        app.clear();
        app.draw(bg);
        sf::sleep(sf::seconds(1)); 
        while (auto e = app.pollEvent()) {
            if (e->is<sf::Event::Closed>()) {
                app.close();
            }
        }
    }

    
    return 0;
}

