# Doseln - Medication Management Dashboard

## Overview
This is a JavaFX application that provides a modern medication management dashboard with an exact design match to the provided UI specification.

## Features

### Dashboard Design
- **Modern Green Theme**: Clean, professional design with a green color scheme
- **Two-Column Layout**: Navigation sidebar on the left, main content on the right
- **Responsive Design**: Proper spacing and typography for optimal user experience

### Navigation
- **App Branding**: "Doseln" with pill-shaped logo elements
- **Active State**: Dashboard button highlighted with dark green background
- **Menu Items**: Manage Schedule, Doctor Contacts, Emergency, About Us
- **Sign Out**: Red button at the bottom for user logout

### Welcome Section
- **Personalized Greeting**: "Hello, Majharul!" (or current user)
- **Motivational Message**: "Your Health Matters - Stay on Track!"
- **Live Clock**: Real-time display showing current time in "hh : mm : ss a" format

### Schedule Management
- **Today's Schedule**: Section header with "+ Set Reminder" button
- **Medication Cards**: Dark green cards with time on left, medicine details on right
- **Sample Data**: Four medication reminders with realistic data:
  - Bilastin 250 mg - Take after eating something
  - Vitamin D 1000 IU - Take with breakfast
  - Aspirin 81 mg - Take with water
  - Omeprazole 20 mg - Take before dinner

## Technical Implementation

### Files Modified
1. **dashboard.fxml**: Updated layout structure and styling classes
2. **dashboard.css**: Complete redesign with exact color scheme and styling
3. **DashboardController.java**: Updated to create proper card layout and sample data

### Color Scheme
- **Primary Green**: #4CAF50 (navigation background)
- **Dark Green**: #2E7D32 (active elements, cards)
- **Light Background**: #f8f9fa (main content area)
- **White**: #ffffff (welcome banner, text)
- **Red**: #F44336 (sign out button)

### Typography
- **Font Family**: Poppins (with fallbacks)
- **Font Weights**: Bold, Semi-Bold, Medium, Regular
- **Font Sizes**: 28px (greeting), 24px (headers), 18px (card times), 16px (medicine names)

## Running the Application

### Prerequisites
- Java 21 or higher
- Maven

### Build and Run
```bash
# Compile the project
./mvnw clean compile

# Run the application
./mvnw javafx:run
```

## Design Notes
- All interactive elements have proper hover states
- Cards use subtle shadows for depth
- Rounded corners throughout for modern appearance
- Proper spacing and alignment for professional look
- Responsive layout that adapts to different screen sizes

The dashboard now matches the exact design specification with proper color scheme, typography, and layout structure.
