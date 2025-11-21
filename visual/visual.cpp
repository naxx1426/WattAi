#include <SFML/Graphics.hpp>
#include <iostream>
#include <string>
#include <fstream>
#include <vector>
#include <sstream>

int s = 1200;

// 表示一组 puzzle 动画：包含阶数 N 和多个状态（每个状态是 vector<int> 长度 N*N）
struct PuzzleSequence {
    int n; // 阶数，3 或 4
    std::vector<std::vector<int>> states; // 每个 state 是 1D 状态
};

std::vector<PuzzleSequence> readtxt(const std::string& filename) {
    std::vector<PuzzleSequence> allSequences;
    std::ifstream infile(filename);
    if (!infile.is_open()) {
        std::cout << "Error opening file" << std::endl;
        return allSequences;
    }

    std::string line;
    while (std::getline(infile, line)) {
        // 跳过空行
        if (line.empty()) continue;

        // 第一行是阶数 N
        int n = std::stoi(line);
        PuzzleSequence seq;
        seq.n = n;

        while (std::getline(infile, line)) {
            if (line == "finish") {
                break;
            }
            if (line.empty()) continue;

            std::istringstream iss(line);
            std::vector<int> state;
            int num;
            while (iss >> num) {
                state.push_back(num);
            }

            // 验证长度是否为 n*n
            if (static_cast<int>(state.size()) != n * n) {
                std::cerr << "Warning: Invalid state size " << state.size()
                          << " for n=" << n << ". Skipping." << std::endl;
                continue;
            }
            seq.states.push_back(state);
        }

        if (!seq.states.empty()) {
            allSequences.push_back(seq);
        }
    }

    return allSequences;
}

void drawOneStep(sf::RenderWindow& app, sf::RectangleShape rect[], const std::vector<int>& state, int n) {
    // 根据当前状态摆放图块
    for (int i = 0; i < n * n; i++) {
        int tileValue = state[i]; // 哪个图块放在位置 i
        float tileSize = static_cast<float>(s) / n;
        rect[tileValue].setSize({tileSize, tileSize});
        rect[tileValue].setPosition({
            static_cast<float>((i % n) * tileSize),
            static_cast<float>((i / n) * tileSize)
        });
    }

    app.clear();
    // 绘制所有非空白块（假设 0 是空白，可选是否绘制）
    for (int i = 0; i < n * n; i++) {
        // 如果你想隐藏空白块（0），可以跳过 i==0
        // 这里我们绘制所有，包括 0（如果你有 0.png）
        app.draw(rect[i]);
    }
    app.display();
}

// 等待用户一次操作（鼠标点击或按键）
void waitForUserInput(sf::RenderWindow& app) {
    bool waiting = true;
    while (waiting && app.isOpen()) {
        while (auto e = app.pollEvent()) {
            if (e->is<sf::Event::Closed>()) {
                app.close();
                return;
            }
            if (e->is<sf::Event::MouseButtonPressed>() || e->is<sf::Event::KeyPressed>()) {
                waiting = false;
                break;
            }
        }
        sf::sleep(sf::milliseconds(10));
    }
}

int main() {
    sf::RenderWindow app(sf::VideoMode({s, s}), "N-Puzzle Animator");
    app.setFramerateLimit(60);

    // 读取所有 puzzle 序列
    std::vector<PuzzleSequence> sequences = readtxt("..\\resources\\output.txt");
    // std::vector<PuzzleSequence> sequences = readtxt("..\\output.txt");

    if (sequences.empty()) {
        std::cout << "No valid puzzle sequences found." << std::endl;
        return -1;
    }

    // 预加载纹理（最多支持 16 个图块：0~15）
    sf::RectangleShape rect[16];
    sf::Texture t[16];
    bool textureLoaded[16] = {false};

    for (int i = 0; i < 16; ++i) {
        std::string path = "images/" + std::to_string(i) + ".png";
        if (!t[i].loadFromFile(path)) {
            // 图片加载失败，使用统一灰色
            rect[i].setFillColor(sf::Color(220, 220, 220));
            rect[i].setOutlineThickness(1);
            rect[i].setOutlineColor(sf::Color::Black);
        } else {
            rect[i].setTexture(&t[i]);
        }
    }

    // 依次播放每个序列
    for (const auto& seq : sequences) {
        int n = seq.n;
        if (n != 3 && n != 4) {
            std::cout << "Unsupported puzzle size: " << n << ". Skipping." << std::endl;
            continue;
        }

        std::cout << "Playing " << n << "-puzzle sequence (" << seq.states.size() << " steps)..." << std::endl;

        for (const auto& state : seq.states) {
            drawOneStep(app, rect, state, n);
            waitForUserInput(app);
            if (!app.isOpen()) return 0;
        }

        // 一组结束后，可选提示
        std::cout << "Finished sequence for " << n << "-puzzle. Next group will start on input..." << std::endl;
        waitForUserInput(app);
        if (!app.isOpen()) return 0;
    }

    std::cout << "All sequences played." << std::endl;
    app.close();
    return 0;
}
