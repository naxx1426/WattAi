#include <SFML/Graphics.hpp>
#include <iostream>
#include <string>
#include <fstream>
#include <vector>
#include <sstream>

const int s = 1200;

std::vector<std::vector<int>> readtxt(const std::string& filename) {
    std::vector<std::vector<int>> nums;
    std::ifstream infile(filename);
    if (!infile.is_open()) {
        std::cout << "Error opening file" << std::endl;
        return nums;
    }
    std::string line;
    while (std::getline(infile, line)) {
        std::istringstream iss(line);
        std::vector<int> row;
        int num;
        while (iss >> num) {
            row.push_back(num);
        }
        nums.push_back(row);
    }
    return nums;
}

int main() {
    sf::RenderWindow app(sf::VideoMode({s, s}), "n-puzzle");
    app.setFramerateLimit(60);

    std::vector<std::vector<int>> puzzles = readtxt("puzzle.txt");
    if (puzzles.empty()) return -1;

    // 背景
    sf::RectangleShape bg;
    sf::Texture t0;
    if (!t0.loadFromFile("images/0.png")) {
        std::cerr << "Failed to load background image." << std::endl;
        return -1;
    }
    bg.setTexture(&t0);
    bg.setSize(sf::Vector2f(static_cast<float>(s), static_cast<float>(s)));

    // 小方块
    sf::RectangleShape rect[16];
    sf::Texture t[16];
    for (int i = 1; i < 16; ++i) {
        std::string path = "images/" + std::to_string(i) + ".png";
        if (!t[i].loadFromFile(path)) {
            std::cerr << "Failed to load: " << path << std::endl;
        } else {
            rect[i].setTexture(&t[i]);
        }
    }

    const int n = 3;
    const float stepDuration = 0.25f;
    const float waitAfterPuzzle = 0.5f;

    size_t currentPuzzleIndex = 0;
    size_t currentStepIndex = 0;
    bool waitingAfterPuzzle = false;
    sf::Clock clock;

    // 解题过程动画
    while (app.isOpen()) {
        sf::Time elapsed = clock.getElapsedTime();
        float dt = elapsed.asSeconds();

        while (auto e = app.pollEvent()) {
            if (e->is<sf::Event::Closed>()) {
                app.close();
            }
        }

        if (currentPuzzleIndex >= puzzles.size()) {
            break;
        }

        const auto& currentPuzzle = puzzles[currentPuzzleIndex];
        size_t totalSteps = currentPuzzle.size() / 9;

        // 更新方块位置
        if (!waitingAfterPuzzle && currentStepIndex < totalSteps) {
            for (int i = 0; i < 9; ++i) {
                int tileValue = currentPuzzle[currentStepIndex * 9 + i];
                if (tileValue >= 1 && tileValue <= 15) {
                    float tileSize = static_cast<float>(s) / n;
                    rect[tileValue].setSize(sf::Vector2f(tileSize, tileSize));
                    rect[tileValue].setPosition(sf::Vector2f(
                        static_cast<float>((i % n) * tileSize),
                        static_cast<float>((i / n) * tileSize)
                    ));
                }
            }
        }

        // 画图
        app.clear();
        app.draw(bg);
        for (int i = 1; i < 16; ++i) {
            app.draw(rect[i]);
        }
        app.display();

        // 时间控制
        if (waitingAfterPuzzle) {
            if (dt >= waitAfterPuzzle) {
                waitingAfterPuzzle = false;
                currentPuzzleIndex++;
                currentStepIndex = 0;
                clock.restart();
            }
        } else {
            if (currentStepIndex < totalSteps) {
                if (dt >= stepDuration) {
                    currentStepIndex++;
                    clock.restart();
                    if (currentStepIndex >= totalSteps) {
                        waitingAfterPuzzle = true;
                        clock.restart();
                    }
                }
            } else {
                waitingAfterPuzzle = true;
                clock.restart();
            }
        }
    }

    return 0;
}
