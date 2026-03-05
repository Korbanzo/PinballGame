# Pinball Game 🎮

A custom pinball game built in Java featuring custom physics simulation. Experience fast-paced pinball action with realistic ball dynamics!

## About

This is a fully functional pinball game implementation that showcases:
- **Custom Physics Engine** - Built-from-scratch physics simulation for realistic ball behavior
- **Interactive Gameplay** - Control flippers to keep the ball in play
- **Pure Java Implementation** - No external game engines, just pure Java code

## Features

✨ **Core Gameplay**
- Dynamic ball physics with gravity, momentum, and friction
- Responsive flipper controls
- Scoring system
- Multiple play mechanics

🎯 **Custom Physics**
- Custom-built physics engine (not using external libraries)
- Realistic collision detection
- Smooth ball trajectory calculations
- Velocity and acceleration systems

## Technologies

- **Language:** Java
- **Build Tool:** Maven
- **IDE:** NetBeans (includes nbactions.xml configuration)

## Getting Started

### Prerequisites

- Java Development Kit (JDK) 8 or higher
- Maven 3.6 or higher

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/Korbanzo/PinballGame.git
   cd PinballGame
   ```

2. **Build the project**
   ```bash
   mvn clean install
   ```

3. **Run the game**
   ```bash
   mvn exec:java
   ```

### Using NetBeans

If you're using NetBeans, the project includes `nbactions.xml` for easy project management:
- Open the project in NetBeans
- Click "Run Project" to start the game

## Project Structure

```
PinballGame/
├── src/                    # Source code
├── pom.xml                # Maven project configuration
├── nbactions.xml          # NetBeans IDE actions
└── .gitignore            # Git ignore rules
```

## How to Play

1. Launch the game
2. Use **Q/E Keys** (or your configured controls) to operate the flippers
3. Keep the ball in play and earn points
4. Try to hit targets and maximize your score!

## Physics Implementation

One of the highlights of this project is the custom-built physics engine. Rather than relying on external physics libraries, the game implements its own calculations for:

- Ball movement and acceleration
- Collision response
- Friction and energy loss
- Gravity simulation
- Flipper interaction

This provides both educational value and a personalized gameplay experience.

## Future Enhancements

- [ ] Multiple ball modes
- [ ] Bonus rounds and special targets
- [ ] Sound effects
- [ ] Leaderboard system
- [ ] Additional table designs

## Contributing

Contributions are welcome! Feel free to:
- Report bugs
- Suggest new features
- Submit pull requests

## License

This project is currently unlicensed. Feel free to reach out if you'd like to discuss licensing options.

## Author

Created by [Korbanzo](https://github.com/Korbanzo)

---

**Enjoy the game and have fun exploring custom physics simulation!** 🎯
