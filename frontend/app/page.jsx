import Features from "@/components/landing/Features";
import Hero from "@/components/landing/Hero";
import Infographic from "@/components/landing/Infographic";
import  Footer  from "@/components/landing/Footer";

export default function Home() {
  // return <LoginPage />
  return (
    <div className="flex flex-col min-h-screen bg-gray-50">
      <Hero />

      <div className="max-w-3xl mx-auto px-4 mt-4 animate-fade-in">
        <Infographic />
      </div>

      <div className="max-w-4xl mx-auto px-4 mt-10">
        <Features />
      </div>

      <Footer />
    </div>
  );
}
