# Data Ingestion Frontend

A Next.js application that provides a user interface for the data ingestion platform, allowing users to upload and process flat files for ingestion into ClickHouse.

## Technology Stack

- Next.js 15
- React 18
- Tailwind CSS
- Radix UI components
- React Hook Form with Zod validation
- Recharts for data visualization

## Prerequisites

- Node.js (version 18 or later recommended)
- pnpm (recommended) or npm

## Getting Started

1. Clone the repository
2. Navigate to the frontend directory
3. Install dependencies:

```bash
pnpm install
# or
npm install
```

4. Create a `.env` file with the following variables:
```
NEXT_PUBLIC_API_URL=http://localhost:8080/api
```

## Development

Run the development server:

```bash
pnpm dev
# or
npm run dev
```

Open [http://localhost:3000](http://localhost:3000) with your browser to see the result.

## Build for Production

```bash
pnpm build
# or
npm run build
```

## Start Production Server

```bash
pnpm start
# or
npm start
```

## Features

- User authentication and authorization
- File upload interface
- Data preview and validation
- Integration with backend API
- Dashboard for monitoring ingestion processes
- Support for different data formats
- Responsive design

## Project Structure

- `app/` - Next.js app router pages and routes
- `components/` - React components
  - `ui/` - Reusable UI components
  - Form components for different ingestion methods
- `hooks/` - Custom React hooks
- `lib/` - Utility functions
- `public/` - Static assets
- `styles/` - Global CSS styles

## Forms

The application includes several specialized forms:
- `clickhouse-form.jsx` - Direct ingestion to ClickHouse
- `flatfile-form.jsx` - CSV/flat file ingestion
- `data-transfer-form.jsx` - General data transfer configuration