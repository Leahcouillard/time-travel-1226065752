#include <iostream>
#include <string>
using namespace std;

void programmingLanguageQuiz() {
    int python = 0, cpp = 0, javascript = 0, java = 0, rust = 0, php = 0;
    string answer;
    cout << "Welcome to the 'Which Programming Language Are You' Quiz! \n";

    //Question 1
    cout << "Q1: What’s your ideal work environment?\n";
    cout << "A. Collaborative and open to everyone\n";
    cout << "B. Structured and disciplined\n";
    cout << "C. Flexible and ever-changing\n";
    cout << "D. Efficient and minimalist\n";
    cout << "E. Complex but rewarding\n";
    cout << "F. Thoughtful and safe\n";
    cout << "Your choice (A/B/C/D/E/F): ";
    cin >> answer;

    if (answer == "A" || answer == "a") python += 2;
    else if (answer == "B" || answer == "b") java += 1;
    else if (answer == "C" || answer == "c") javascript += 1;
    else if (answer == "D" || answer == "d") rust += 2;
    else if (answer == "E" || answer == "e") cpp += 2;
    else if (answer == "F" || answer == "f") rust += 2;

    // Question 2
    cout << "\nQ2: What’s your reaction to bugs in your code?\n";
    cout << "A. 'Oops! Let’s debug this together.'\n";
    cout << "B. 'I’ll run through every line until I find it.'\n";
    cout << "C. 'Just refresh the page. It might fix itself!'\n";
    cout << "D. 'It’s okay, the compiler won’t let it through.'\n";
    cout << "E. 'I’ll log everything and figure it out systematically.'\n";
    cout << "F. 'I’ll Google it and hope for the best.'\n";
    cout << "Your choice (A/B/C/D/E/F): ";
    cin >> answer;

    if (answer == "A" || answer == "a") python += 2;
    else if (answer == "B" || answer == "b") cpp += 1;
    else if (answer == "C" || answer == "c") javascript += 1;
    else if (answer == "D" || answer == "d") rust += 2;
    else if (answer == "E" || answer == "e") java += 1;
    else if (answer == "F" || answer == "f") php += 1;

    // Question 3
    cout << "\nQ3: What’s your favorite coding aesthetic?\n";
    cout << "A. Clean and readable\n";
    cout << "B. Logical and verbose\n";
    cout << "C. Fast and hacky\n";
    cout << "D. Safe and strict\n";
    cout << "E. Efficient and low-level\n";
    cout << "F. Functional and elegant\n";
    cout << "Your choice (A/B/C/D/E/F): ";
    cin >> answer;

    if (answer == "A" || answer == "a") python += 2;
    else if (answer == "B" || answer == "b") java += 1;
    else if (answer == "C" || answer == "c") javascript += 1;
    else if (answer == "D" || answer == "d") rust += 2;
    else if (answer == "E" || answer == "e") cpp += 2;
    else if (answer == "F" || answer == "f") php += 0;

    // Results
    cout << "\nCalculating your programming language...\n";

    if (python >= cpp && python >= javascript && python >= java && python >= rust && python >= php) {
        cout << "You are Python: Friendly, versatile, and intuitive!\n";
    } else if (cpp >= python && cpp >= javascript && cpp >= java && cpp >= rust && cpp >= php) {
        cout << "You are C++: Serious, powerful, and a little complex.\n";
    } else if (javascript >= python && javascript >= cpp && javascript >= java && javascript >= rust && javascript >= php) {
        cout << "You are JavaScript: Dynamic, quirky, and creative!\n";
    } else if (java >= python && java >= cpp && java >= javascript && java >= rust && java >= php) {
        cout << "You are Java: Organized, structured, and reliable.\n";
    } else if (rust >= python && rust >= cpp && rust >= javascript && rust >= java && rust >= php) {
        cout << "You are Rust: Safe, modern, and a bit strict.\n";
    } else {
        cout << "You are PHP: Practical but divisive. Love you or hate you, you get the job done!\n";
    }
}

int main() {
    programmingLanguageQuiz();
    return 0;
}
